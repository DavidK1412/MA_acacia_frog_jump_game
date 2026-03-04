# Implementación del Endpoint /v1/graph/metrics

## Resumen

Se ha implementado el endpoint `POST /v1/graph/metrics` según el spec definido en `specs/endpoint/ide_agent_spec_metrics_branching_cyclicity_en.md`, siguiendo la arquitectura limpia (Clean Architecture) ya establecida en el proyecto.

## Estructura de la Implementación

### 1. Configuración de Base de Datos

**Archivos modificados:**
- `.env.example`: Plantilla con `DATABASE_URL`
- `.env`: Archivo local (en `.gitignore`)
- `internal/infrastructure/config/config.go`: Añadido `DatabaseConfig` con campo `URL`

**Variables de entorno:**
```env
SERVER_PORT=8080
DATABASE_URL=postgres://user:password@localhost:5432/frog_game?sslmode=disable
```

### 2. Capa de Dominio

**Nuevas entidades** (`internal/domain/game/entity/metrics.go`):
- `MetricsResult`: Resultado completo de las métricas
- `BranchingMetrics`: Métricas de ramificación (local, step_count, jump_count)
- `CyclicityMetrics`: Métricas de ciclicidad (total_moves, visited_states, unique_states, repeated_states, cyclicity)

**Nuevo repositorio** (`internal/domain/game/repository/movement_repository.go`):
- `MovementRepository`: Interface con método `GetCyclicityMetrics(ctx, attemptID)`

### 3. Capa de Infraestructura

**Implementación del repositorio** (`internal/infrastructure/repository/postgres_movement_repository.go`):
- `PostgresMovementRepository`: Implementa `MovementRepository`
- Usa `pgxpool` para conexión con PostgreSQL
- Query SQL según spec con cálculo de ciclicidad

**Dependencias añadidas:**
- `github.com/jackc/pgx/v5/pgxpool` (v5.8.0)

### 4. Capa de Aplicación

**Nuevo caso de uso** (`internal/application/game/usecase/metrics_usecase.go`):
- `MetricsUseCase`: Coordina el cálculo de branching y cyclicity
- Usa `LegalMovesGenerator` para calcular branching local
- Usa `MovementRepository` para obtener cyclicity desde BD
- Incluye timeout de 5 segundos para queries a BD

### 5. Capa de API

**Nuevo handler** (`internal/api/handler/metrics_handler.go`):
- `MetricsHandler`: Maneja peticiones HTTP a `/v1/graph/metrics`
- Validación de request (attempt_id, state)
- DTOs para request/response en formato snake_case JSON
- Manejo de errores: `400 invalid_state`, `404 attempt_not_found`

**Router actualizado** (`internal/api/router/router.go`):
- Ruta añadida: `POST /v1/graph/metrics`
- Manejo condicional si `metricsHandler` es nil (cuando no hay DATABASE_URL)

### 6. Main

**Cambios en** `main.go`:
- Inicialización condicional de `pgxpool.Pool` si `DATABASE_URL` está configurado
- Ping a BD al inicio para verificar conexión
- Creación de `LegalMovesGenerator`, `MovementRepository`, `MetricsUseCase` y `MetricsHandler`
- Graceful shutdown incluye cierre del pool de BD
- Log de advertencia si DATABASE_URL no está configurado

## Características Implementadas

### Validación de Estado (400 invalid_state)

El endpoint valida que el estado cumpla con:
1. Contiene exactamente un `0`
2. Sin valores duplicados
3. `max_id = max(state)` es par
4. `len(state) = max_id + 1`
5. Conjunto exacto `{0, 1, 2, ..., 2N}`

### Cálculo de Branching

- **Local**: Número total de movimientos legales desde el estado
- **Step Count**: Número de movimientos tipo STEP
- **Jump Count**: Número de movimientos tipo JUMP

Usa las reglas del juego implementadas en `LegalMovesGenerator`.

### Cálculo de Cyclicity desde PostgreSQL

Query SQL según spec:
```sql
SELECT
  COUNT(*) - 1 AS total_moves,
  COUNT(*)     AS visited_states,
  COUNT(DISTINCT state_hash) AS unique_states,
  (COUNT(*) - COUNT(DISTINCT state_hash)) AS repeated_states,
  CASE
    WHEN COUNT(*) - 1 = 0 THEN 0
    ELSE (COUNT(*) - COUNT(DISTINCT state_hash))::float / (COUNT(*) - 1)
  END AS cyclicity
FROM movements
WHERE attempt_id = $1
```

**Fórmula de cyclicity:**
```
cyclicity = repeated_states / total_moves
```

### Respuestas

**200 OK - Ejemplo:**
```json
{
  "attempt_id": "8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999",
  "level": 3,
  "branching": {
    "local": 2,
    "step_count": 2,
    "jump_count": 0
  },
  "cyclicity": {
    "total_moves": 20,
    "visited_states": 21,
    "unique_states": 15,
    "repeated_states": 6,
    "cyclicity": 0.3
  },
  "meta": {
    "source": "db",
    "time_ms": 6
  }
}
```

**404 attempt_not_found:**
```json
{
  "error": "attempt_not_found",
  "message": "no movements found for attempt_id."
}
```

**400 invalid_state:**
```json
{
  "error": "invalid_state",
  "message": "state must contain exactly once each value from 0..2N and exactly one zero."
}
```

## Schema de Base de Datos

### Tabla `movements`

La tabla debe tener la siguiente estructura (según spec, los alters ya están hechos):

```sql
CREATE TABLE movements (
  id SERIAL PRIMARY KEY,
  attempt_id UUID NOT NULL,
  step INT NOT NULL,
  state_hash VARCHAR(64) NOT NULL,
  -- otros campos...
);

CREATE INDEX IF NOT EXISTS idx_movements_attempt_step
  ON movements(attempt_id, step);

CREATE INDEX IF NOT EXISTS idx_movements_attempt_state_hash
  ON movements(attempt_id, state_hash);
```

**Convención importante:**
- `step = 0` representa el estado INICIAL (INIT)
- Movimientos reales tienen `step >= 1`

## Pruebas Recomendadas

### Casos de Prueba según Spec

| ID | Caso | Input | Esperado |
|---:|---|---|---|
| M01 | branching N=3 initial | `state=[1,2,3,0,4,5,6]` | `branching.local=2`, `step_count=2`, `jump_count=0` |
| M02 | branching N=4 initial | `state=[1,2,3,4,0,5,6,7,8]` | `branching.local=2` |
| M03 | branching with 3 options | `state=[1,2,0,3,4,5,6]` | `branching.local=3` |
| M04 | invalid state (missing 0) | `state=[1,2,3,4,5,6,7]` | `400 invalid_state` |
| M05 | cyclicity with INIT | attempt has steps `0..M` | `total_moves=count-1`, cyclicity correct |
| M06 | cyclicity M=0 | attempt only has `step=0` | `total_moves=0`, `cyclicity=0` |
| M07 | repeated states | repeated hashes present | `repeated_states = visited_states - unique_states` |
| M08 | attempt not found | attempt_id has no rows | `404 attempt_not_found` |
| M09 | performance | large attempt | query < 50ms (same region) |
| M10 | consistency | repeated calls | stable metrics for same attempt_id/state |

### Ejemplo de Request

```bash
curl -X POST http://localhost:8080/v1/graph/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "attempt_id": "8a0f7e9b-2f5a-4c71-9c0f-6d9b7332a999",
    "state": [1, 2, 3, 0, 4, 5, 6],
    "options": {
      "return_meta": true
    }
  }'
```

## Compilación y Ejecución

```bash
# Instalar dependencias
go mod download

# Compilar
go build -o bin/graph-api.exe .

# Ejecutar
./bin/graph-api.exe
```

## Notas Importantes

1. **DATABASE_URL opcional**: Si no se configura `DATABASE_URL`, la aplicación inicia pero el endpoint `/v1/graph/metrics` no estará disponible (handler será nil).

2. **Clean Architecture**: Se siguió estrictamente la arquitectura limpia del proyecto:
   - Domain: Entidades y repositorios (interfaces)
   - Application: Casos de uso
   - Infrastructure: Implementaciones concretas (PostgreSQL)
   - API: Handlers y router

3. **Reutilización**: Se reutiliza `LegalMovesGenerator` existente para calcular branching, siguiendo las reglas del juego ya implementadas.

4. **Manejo de errores**: Todos los errores devuelven códigos HTTP apropiados y mensajes descriptivos.

5. **Performance**: Query optimizado con índices sugeridos en spec para consultas rápidas (<50ms).
