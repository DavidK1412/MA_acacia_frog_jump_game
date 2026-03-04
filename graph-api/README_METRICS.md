# Endpoint /v1/graph/metrics - Guía Rápida

## Configuración Inicial

### 1. Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
SERVER_PORT=8080
DATABASE_URL=postgres://user:password@localhost:5432/frog_game?sslmode=disable
```

### 2. Base de Datos PostgreSQL

Ejecuta el script SQL para crear la tabla y datos de prueba:

```bash
psql -U user -d frog_game -f test_data.sql
```

O si usas pgAdmin o DBeaver, ejecuta el contenido de `test_data.sql`.

### 3. Compilar y Ejecutar

```bash
# Compilar
go build -o bin/graph-api.exe .

# Ejecutar
./bin/graph-api.exe
```

## Pruebas Rápidas

### PowerShell (Windows)

```powershell
.\test_metrics.ps1
```

### Bash (Linux/Mac)

```bash
chmod +x test_metrics.sh
./test_metrics.sh
```

### Prueba Manual con cURL

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

**Respuesta esperada:**

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
    "time_ms": 5
  }
}
```

## Casos de Prueba

### Test 1: Estado inicial N=3 ✅
- **State**: `[1, 2, 3, 0, 4, 5, 6]`
- **Branching esperado**: `local=2`, `step_count=2`, `jump_count=0`

### Test 2: Estado inválido ❌
- **State**: `[1, 2, 3, 4, 5, 6, 7]` (sin cero)
- **Esperado**: `400 invalid_state`

### Test 3: Estado con 3 opciones ✅
- **State**: `[1, 2, 0, 3, 4, 5, 6]`
- **Branching esperado**: `local=3`

### Test 4: Attempt no encontrado ❌
- **attempt_id**: `"non-existent-uuid"`
- **Esperado**: `404 attempt_not_found`

## Estructura del Proyecto

```
graph-api/
├── internal/
│   ├── domain/
│   │   └── game/
│   │       ├── entity/
│   │       │   └── metrics.go          (✨ NUEVO)
│   │       └── repository/
│   │           └── movement_repository.go (✨ NUEVO)
│   ├── application/
│   │   └── game/
│   │       └── usecase/
│   │           └── metrics_usecase.go  (✨ NUEVO)
│   ├── infrastructure/
│   │   ├── config/
│   │   │   └── config.go               (📝 MODIFICADO)
│   │   └── repository/
│   │       └── postgres_movement_repository.go (✨ NUEVO)
│   └── api/
│       ├── handler/
│       │   └── metrics_handler.go      (✨ NUEVO)
│       └── router/
│           └── router.go               (📝 MODIFICADO)
├── main.go                             (📝 MODIFICADO)
├── .env                                (✨ NUEVO)
├── .env.example                        (📝 MODIFICADO)
└── test_data.sql                       (✨ NUEVO)
```

## Documentación Completa

Ver `IMPLEMENTACION_METRICS.md` para documentación detallada de la implementación.
