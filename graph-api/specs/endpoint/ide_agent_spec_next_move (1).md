# IDE Agent Spec — Graph Service (Go) /next-move (POLICY_TABLE precalculada)

Este documento define el contrato y criterios que un IDE/agente (o tú mismo) debe seguir para implementar el endpoint **/v1/graph/next-move** de forma **segura, retrocompatible por nivel** y **sin devolver movimientos ilegales**, usando **tablas precalculadas** (policy tables) para que el servicio sea ideal para **serverless**.

---

## 1) Objetivo

Implementar un endpoint HTTP que, dado un **estado actual** del juego (lista de enteros), devuelva el **mejor siguiente movimiento** hacia el estado objetivo del nivel.

**Requisitos clave:**
- Nunca devolver un movimiento que viole las reglas.
- Soportar múltiples niveles (N variable) sin cambiar el contrato.
- **Estrategia primaria:** lookup en **POLICY_TABLE precalculada** por nivel (O(1)).
- **Serverless-friendly:** el precálculo NO se hace en runtime de la función; se carga desde archivos (repo/artefacto) o embebidos en el binario.

---

## 2) Endpoint

**POST** `/v1/graph/next-move`

### Request JSON
```json
{
  "state": [1, 2, 3, 0, 4, 5, 6],
  "goal": "default",
  "options": {
    "returnNextState": true,
    "returnMeta": true
  }
}
```

#### Campos
- `state` (required): array de enteros.
- `goal` (optional): `"default"` por ahora.
- `options` (optional):
  - `returnNextState`: incluye `nextState` en response.
  - `returnMeta`: incluye `meta`.

> Nota: `limits` queda reservado para futuros fallbacks, pero la estrategia primaria no debería necesitarlo.

---

## 3) Response

### 200 OK — found=true
```json
{
  "found": true,
  "level": 3,
  "nextMove": {
    "type": "STEP",
    "frogId": 3,
    "fromIndex": 2,
    "toIndex": 3
  },
  "nextState": [1, 2, 0, 3, 4, 5, 6],
  "meta": {
    "goalState": [4, 5, 6, 0, 1, 2, 3],
    "predictedRemainingCost": 14,
    "legalMovesFromState": 2,
    "strategy": "POLICY_TABLE",
    "policyVersion": "level_3_v1",
    "loadSource": "EMBEDDED",
    "timeMs": 1
  }
}
```

### 200 OK — found=false (estado válido pero no tiene solución / no está en la tabla)
```json
{
  "found": false,
  "level": 5,
  "reason": "NOT_REACHABLE",
  "meta": {
    "strategy": "POLICY_TABLE",
    "policyVersion": "level_5_v1"
  }
}
```

#### reason (sugeridos)
- `NOT_REACHABLE` (si el estado no es alcanzable al goal según la tabla)
- `POLICY_NOT_AVAILABLE` (no existe tabla para ese nivel)
- `INTERNAL_ERROR`

---

## 4) Validación obligatoria de estado (server-side)

**El servidor debe rechazar con 400** si:

1. `state` no contiene `0` exactamente una vez.
2. hay números repetidos.
3. `maxID = max(state)` no cumple `maxID % 2 == 0` (porque `maxID = 2N`).
4. `len(state) != maxID + 1` (equivale a `2N + 1`).
5. el conjunto no es exactamente `{0, 1, 2, ..., 2N}`.

### 400 Bad Request ejemplo
```json
{
  "error": "INVALID_STATE",
  "message": "State must contain exactly once each value from 0..2N and exactly one zero.",
  "details": {
    "missing": [5],
    "duplicated": [2],
    "expectedLength": 11,
    "actualLength": 10
  }
}
```

---

## 5) Detección automática del nivel (retrocompatibilidad)

- `N = max(state) / 2`
- `level = N`
- `goalState` por defecto:
  - `[N+1, ..., 2N, 0, 1, ..., N]`

Ejemplos:
- N=3 → goal: `[4,5,6,0,1,2,3]`
- N=4 → goal: `[5,6,7,8,0,1,2,3,4]`
- N=5 → goal: `[6,7,8,9,10,0,1,2,3,4,5]`

---

## 6) ¿Qué es POLICY_TABLE? (normativo)

Una **policy table** por nivel `N` es un artefacto precalculado que permite responder **/next-move** sin hacer BFS por request.

Debe contener, como mínimo:

- `dist[id]`: distancia mínima al goal (entero, puede ser `uint16/uint32`)
- `next[id]`: **siguiente estado óptimo** (o el movimiento) para acercarse al goal

Opcional (pero recomendado):
- `degree[id]`: número de movimientos legales desde el estado (ramificación local)

En runtime, el servicio hace:
1) `id = stateToID(state)`
2) `nextID = next[id]`
3) reconstruye el movimiento comparando `state` vs `nextState` (o lo lee directo si la tabla ya guarda el move)

---

## 7) Cómo se genera la tabla (offline) — Reverse BFS

La tabla se genera **fuera del runtime serverless** (en un job, build step, o herramienta local):

1) Detectar nivel `N`
2) Definir `goalState`
3) Recorrer el grafo haciendo **BFS desde el goal** (reverse BFS)
4) Para cada estado `s` descubierto desde `goal`:
   - `dist[s] = dist[parent] + 1`
   - `next[s] = parent`  (porque `parent` está más cerca del goal)

Esto produce `dist/next` para todos los estados alcanzables del nivel.

---

## 8) Distribución del precálculo en serverless (obligatorio)

El runtime serverless NO debe recalcular la policy table.

Se permiten dos formas (elige una):

### A) Archivos en el repo/artefacto
```
/graphs/
  level_3.bin
  level_4.bin
  level_5.bin
```
El servicio carga esos archivos al iniciar.

### B) Embebido en el binario Go (recomendado)
Usar `//go:embed` para incluir `graphs/level_*.bin` dentro del ejecutable.

**Meta recomendado**
- `meta.loadSource = "EMBEDDED"` o `"FILESYSTEM"`

---

## 9) Contrato del movimiento (normativo)

`nextMove` debe ser un movimiento legal y consistente con `nextState` (si se devuelve).

```json
"nextMove": {
  "type": "STEP" | "JUMP",
  "frogId": number,
  "fromIndex": number,
  "toIndex": number
}
```

**Invariantes:**
- `toIndex` debe ser la posición actual del `0`.
- aplicar `nextMove` al estado debe producir `nextState`.
- `nextState` debe ser una permutación válida de `{0..2N}`.

---

## 10) Reglas para reconstruir el move desde state -> nextState

Si la policy guarda solo `nextState`:

- Encuentra `fromIndex` y `toIndex` (posición del `0` en `state` y `nextState`)
- `frogId` = valor que se movió al hueco
- `type`:
  - `STEP` si `|fromIndex - toIndex| == 1`
  - `JUMP` si `|fromIndex - toIndex| == 2`

Luego valida reglas de dirección y salto antes de responder.

---

## 11) Tabla de tests (mínimo requerido)

| ID | Nivel N | Estado de entrada | Caso | Esperado |
|---:|:------:|---|---|---|
| T01 | 3 | `[1,2,3,0,4,5,6]` | Estado válido inicial | `200`, `found=true`, `level=3`, `strategy=POLICY_TABLE`, `nextMove` legal |
| T02 | 4 | `[1,2,3,4,0,5,6,7,8]` | Estado válido inicial | `200`, `found=true`, `level=4`, `strategy=POLICY_TABLE`, `nextMove` legal |
| T03 | 5 | `[1,2,3,4,5,0,6,7,8,9,10]` | Estado válido inicial | `200`, `found=true`, `level=5`, `strategy=POLICY_TABLE`, `nextMove` legal |
| T04 | 3 | `[1,2,3,4,5,6]` | Longitud inválida | `400 INVALID_STATE` |
| T05 | 3 | `[1,2,3,0,4,5,5]` | Duplicado | `400 INVALID_STATE` |
| T06 | 3 | `[1,2,3,4,5,6,7]` | Sin `0` | `400 INVALID_STATE` |
| T07 | 3 | `[0,1,2,3,4,5,6]` | Orden distinto, válido | `200`, `strategy=POLICY_TABLE`, `nextMove` legal |
| T08 | 3 | `returnNextState=true` | Consistencia | `nextState` consistente con `nextMove` y válido |
| T09 | 6+ | Estado válido con nivel sin tabla | Policy no disponible | `200`, `found=false`, `reason=POLICY_NOT_AVAILABLE` |
| T10 | 3 | Estado válido fuera de tabla | Not reachable | `200`, `found=false`, `reason=NOT_REACHABLE` |
| T11 | 3 | Estado válido | Nunca movimiento ilegal | `nextMove` siempre cumple reglas STEP/JUMP y dirección |
| T12 | 3 | Carga embebida | Startup | `loadSource=EMBEDDED`, latencia baja |
| T13 | 3 | Carga filesystem | Startup | `loadSource=FILESYSTEM`, funciona con paths correctos |

---

## 12) Checklist del agente IDE (Definition of Done)

- [ ] Request/Response implementados según spec
- [ ] Validación estricta del estado con errores 400
- [ ] Detección de N automática
- [ ] Loader de policy tables por nivel (archivos o embed)
- [ ] Lookup `state -> id -> next` (sin BFS por request)
- [ ] Reconstrucción de `nextMove` desde `state -> nextState`
- [ ] Validación final del `nextMove` (anti-movimientos ilegales)
- [ ] Tests para N=3/4/5 y casos inválidos
- [ ] Observabilidad mínima (`strategy`, `policyVersion`, `loadSource`, `timeMs`)
