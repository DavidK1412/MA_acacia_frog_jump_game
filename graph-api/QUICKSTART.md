# Quick Start Guide

## 🚀 Inicio Rápido (5 pasos)

### 1. Instalar dependencias
```bash
go mod download
```

### 2. Ejecutar la aplicación
```bash
go run main.go
```

### 3. Verificar que funciona
Abre tu navegador en: http://localhost:8080/ping

O usa curl:
```bash
curl http://localhost:8080/ping
```

Deberías ver:
```json
{
  "status": "healthy",
  "message": "pong",
  "timestamp": "2026-03-01T17:30:00-05:00"
}
```

### 4. Compilar (opcional)
```bash
make build
# o
go build -o bin/api main.go
```

### 5. Ejecutar binario (opcional)
```bash
# Windows
.\bin\api.exe

# Linux/Mac
./bin/api
```

## 📝 Comandos Útiles

### Desarrollo
```bash
make run          # Ejecutar aplicación
make build        # Compilar binario
make test         # Ejecutar tests
make test-coverage # Tests con cobertura
make fmt          # Formatear código
make tidy         # Limpiar dependencias
make clean        # Limpiar archivos generados
```

### Sin Make
```bash
go run main.go              # Ejecutar
go build -o bin/api main.go # Compilar
go test ./...               # Tests
go fmt ./...                # Formatear
go mod tidy                 # Limpiar deps
```

## 🎯 Arquitectura en 3 Capas

1. **Domain** (`internal/domain/`) - Lógica de negocio pura
2. **Application** (`internal/application/`) - Casos de uso
3. **Infrastructure** (`internal/infrastructure/`) - HTTP, DB, etc.

## 📚 Documentación

- `README.md` - Introducción y overview
- `ARCHITECTURE.md` - Detalles de arquitectura y DDD
- `STRUCTURE.md` - Estructura de directorios
- `EXAMPLES.md` - Ejemplos de uso y desarrollo
- `QUICKSTART.md` - Esta guía rápida

## 🔧 Configuración

Variables de entorno (crear `.env`):
```bash
SERVER_PORT=8080
```

O ejecutar con:
```bash
SERVER_PORT=3000 go run main.go
```

## ✅ Verificar Instalación

Ejecuta esto para verificar que todo funciona:

```bash
# Terminal 1: Iniciar servidor
go run main.go

# Terminal 2: Probar endpoint
curl http://localhost:8080/ping
```

Si ves el JSON con "message": "pong", ¡todo está funcionando! 🎉

## 🛠️ Próximos Pasos

1. Lee `ARCHITECTURE.md` para entender la estructura
2. Lee `EXAMPLES.md` para ver cómo agregar endpoints
3. Revisa el código en `internal/` para familiarizarte
4. Agrega tu primer endpoint siguiendo el patrón existente

## ❓ Troubleshooting

### Error: "address already in use"
El puerto 8080 está ocupado. Usa otro puerto:
```bash
SERVER_PORT=3000 go run main.go
```

### Error: "no required module provides package..."
Ejecuta:
```bash
go mod tidy
go mod download
```

### Error compilando
Verifica tu versión de Go:
```bash
go version  # Debería ser >= 1.23
```

## 📦 Dependencias

- **gorilla/mux**: Router HTTP
- **zap**: Logger estructurado

Todas se instalan automáticamente con `go mod download`.

---

**¿Listo para empezar?** Ejecuta `go run main.go` y visita http://localhost:8080/ping 🚀
