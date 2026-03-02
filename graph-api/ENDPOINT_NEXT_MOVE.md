# Endpoint /v1/graph/next-move - Ejemplos de Uso (POLICY_TABLE)

## Endpoint Implementado

**POST** `/v1/graph/next-move`

Este endpoint encuentra el mejor siguiente movimiento para el juego de las ranas usando **POLICY_TABLE precalculada** (O(1) lookup).

## Estrategia: POLICY_TABLE

El endpoint utiliza tablas precalculadas generadas con **reverse BFS** desde el estado objetivo. Esto permite:
- ✅ **O(1) lookup** - Respuesta instantánea sin búsqueda en runtime
- ✅ **Serverless-friendly** - Sin cálculos pesados por request
- ✅ **Determinista** - Siempre devuelve el camino óptimo
- ✅ **Escalable** - Cargar tablas al inicio, usar en cada request

### Policy Tables Disponibles:
- `level_3.json` - 72 estados (N=3)
- `level_4.json` - 195 estados (N=4)
- `level_5.json` - 476 estados (N=5)

## Ejemplos de Request

### Ejemplo 1: Request Básico (N=3)

```bash
curl -X POST http://localhost:8080/v1/graph/next-move \
  -H "Content-Type: application/json" \
  -d '{
    "state": [1, 2, 3, 0, 4, 5, 6]
  }'
```

**Response:**
```json
{
  "found": true,
  "level": 3,
  "nextMove": {
    "type": "STEP",
    "frogId": 3,
    "fromIndex": 2,
    "toIndex": 3
  }
}
```

### Ejemplo 2: Request con Opciones Completas (N=4)

```bash
curl -X POST http://localhost:8080/v1/graph/next-move \
  -H "Content-Type: application/json" \
  -d '{
    "state": [1, 2, 3, 4, 0, 5, 6, 7, 8],
    "options": {
      "returnNextState": true,
      "returnMeta": true
    }
  }'
```

**Response:**
```json
{
  "found": true,
  "level": 4,
  "nextMove": {
    "type": "STEP",
    "frogId": 4,
    "fromIndex": 3,
    "toIndex": 4
  },
  "nextState": [1, 2, 3, 0, 4, 5, 6, 7, 8],
  "meta": {
    "goalState": [5, 6, 7, 8, 0, 1, 2, 3, 4],
    "predictedRemainingCost": 24,
    "legalMovesFromState": 2,
    "strategy": "BFS",
    "expandedNodes": 193,
    "generatedEdges": 217,
    "timeMs": 1
  }
}
```

### Ejemplo 3: Request con Límites (N=5)

```bash
curl -X POST http://localhost:8080/v1/graph/next-move \
  -H "Content-Type: application/json" \
  -d '{
    "state": [1, 2, 3, 4, 5, 0, 6, 7, 8, 9, 10],
    "limits": {
      "timeoutMs": 100,
      "maxNodes": 10000,
      "maxDepth": 100
    },
    "options": {
      "returnNextState": true,
      "returnMeta": true
    }
  }'
```

**Response:**
```json
{
  "found": true,
  "level": 5,
  "nextMove": {
    "type": "STEP",
    "frogId": 5,
    "fromIndex": 4,
    "toIndex": 5
  },
  "nextState": [1, 2, 3, 4, 0, 5, 6, 7, 8, 9, 10],
  "meta": {
    "goalState": [6, 7, 8, 9, 10, 0, 1, 2, 3, 4, 5],
    "predictedRemainingCost": 48,
    "legalMovesFromState": 2,
    "strategy": "BFS",
    "expandedNodes": 474,
    "generatedEdges": 519,
    "timeMs": 1
  }
}
```

## Ejemplos de Estados Inválidos

### Estado sin cero

```bash
curl -X POST http://localhost:8080/v1/graph/next-move \
  -H "Content-Type: application/json" \
  -d '{
    "state": [1, 2, 3, 4, 5, 6, 7]
  }'
```

**Response (400):**
```json
{
  "error": "INVALID_STATE",
  "message": "state must contain exactly one zero, got 0"
}
```

### Estado con duplicados

```bash
curl -X POST http://localhost:8080/v1/graph/next-move \
  -H "Content-Type: application/json" \
  -d '{
    "state": [1, 2, 3, 0, 4, 5, 5]
  }'
```

**Response (400):**
```json
{
  "error": "INVALID_STATE",
  "message": "duplicate value 5"
}
```

### Longitud incorrecta

```bash
curl -X POST http://localhost:8080/v1/graph/next-move \
  -H "Content-Type: application/json" \
  -d '{
    "state": [1, 2, 3, 0, 4, 5]
  }'
```

**Response (400):**
```json
{
  "error": "INVALID_STATE",
  "message": "state length must be 6, got 6"
}
```

## Ejemplos con JavaScript/Node.js

```javascript
const response = await fetch('http://localhost:8080/v1/graph/next-move', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    state: [1, 2, 3, 0, 4, 5, 6],
    options: {
      returnNextState: true,
      returnMeta: true
    }
  })
});

const data = await response.json();
console.log('Found:', data.found);
console.log('Next Move:', data.nextMove);
console.log('Next State:', data.nextState);
```

## Ejemplos con Python

```python
import requests

response = requests.post(
    'http://localhost:8080/v1/graph/next-move',
    json={
        'state': [1, 2, 3, 0, 4, 5, 6],
        'options': {
            'returnNextState': True,
            'returnMeta': True
        }
    }
)

data = response.json()
print(f"Found: {data['found']}")
print(f"Next Move: {data['nextMove']}")
print(f"Meta: {data['meta']}")
```

## Campos del Request

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `state` | `int[]` | Sí | Estado actual del juego |
| `goal` | `string` | No | Tipo de objetivo (solo "default" por ahora) |
| `limits.timeoutMs` | `int` | No | Timeout en ms (default: 200) |
| `limits.maxNodes` | `int` | No | Máximo de nodos a explorar (default: 200000) |
| `limits.maxDepth` | `int` | No | Profundidad máxima (default: 200) |
| `options.returnNextState` | `bool` | No | Incluir nextState en response |
| `options.returnMeta` | `bool` | No | Incluir metadata en response |

## Campos del Response (Success)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `found` | `bool` | Si se encontró un movimiento |
| `level` | `int` | Nivel detectado (N) |
| `nextMove.type` | `string` | "STEP" o "JUMP" |
| `nextMove.frogId` | `int` | ID de la rana a mover |
| `nextMove.fromIndex` | `int` | Índice origen |
| `nextMove.toIndex` | `int` | Índice destino |
| `nextState` | `int[]` | Estado resultante (si se solicitó) |
| `meta.strategy` | `string` | Estrategia usada ("BFS") |
| `meta.expandedNodes` | `int` | Nodos explorados |
| `meta.generatedEdges` | `int` | Edges generados |
| `meta.timeMs` | `int` | Tiempo de ejecución |

## Reglas del Juego

- Equipo izquierdo: IDs `1..N` (se mueven hacia la derecha)
- Equipo derecho: IDs `N+1..2N` (se mueven hacia la izquierda)
- `0` representa el hueco
- **STEP**: mover 1 casilla hacia el hueco
- **JUMP**: saltar 2 casillas sobre una rana enemiga hacia el hueco
- **Goal**: `[N+1..2N, 0, 1..N]`

### Ejemplos de Estados Iniciales por Nivel

- **N=3**: `[1, 2, 3, 0, 4, 5, 6]` → Goal: `[4, 5, 6, 0, 1, 2, 3]`
- **N=4**: `[1, 2, 3, 4, 0, 5, 6, 7, 8]` → Goal: `[5, 6, 7, 8, 0, 1, 2, 3, 4]`
- **N=5**: `[1, 2, 3, 4, 5, 0, 6, 7, 8, 9, 10]` → Goal: `[6, 7, 8, 9, 10, 0, 1, 2, 3, 4, 5]`
