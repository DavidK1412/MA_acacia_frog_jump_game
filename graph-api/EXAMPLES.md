# Ejemplos de Uso

## Iniciar la Aplicación

### Usando Go directamente
```bash
go run main.go
```

### Usando el binario compilado
```bash
# Compilar
go build -o bin/api main.go

# En Linux/Mac
./bin/api

# En Windows
.\bin\api.exe
```

### Usando Make
```bash
# Desarrollo
make run

# Compilar
make build

# Ejecutar binario
./bin/api
```

## Variables de Entorno

Crear un archivo `.env` basado en `.env.example`:

```bash
cp .env.example .env
```

Configurar puerto personalizado:
```bash
SERVER_PORT=3000 go run main.go
```

## Probar el Endpoint

### Usando curl
```bash
curl http://localhost:8080/ping
```

### Usando PowerShell (Windows)
```powershell
Invoke-WebRequest -Uri http://localhost:8080/ping -Method GET
```

### Usando navegador
Abre tu navegador en: http://localhost:8080/ping

### Respuesta esperada
```json
{
  "status": "healthy",
  "message": "pong",
  "timestamp": "2026-03-01T17:30:00-05:00"
}
```

## Ejemplo con código Go

```go
package main

import (
    "encoding/json"
    "fmt"
    "net/http"
)

type PingResponse struct {
    Status    string `json:"status"`
    Message   string `json:"message"`
    Timestamp string `json:"timestamp"`
}

func main() {
    resp, err := http.Get("http://localhost:8080/ping")
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()

    var result PingResponse
    json.NewDecoder(resp.Body).Decode(&result)
    
    fmt.Printf("Status: %s\n", result.Status)
    fmt.Printf("Message: %s\n", result.Message)
    fmt.Printf("Timestamp: %s\n", result.Timestamp)
}
```

## Ejemplo con JavaScript/Node.js

```javascript
const response = await fetch('http://localhost:8080/ping');
const data = await response.json();

console.log('Status:', data.status);
console.log('Message:', data.message);
console.log('Timestamp:', data.timestamp);
```

## Ejemplo con Python

```python
import requests

response = requests.get('http://localhost:8080/ping')
data = response.json()

print(f"Status: {data['status']}")
print(f"Message: {data['message']}")
print(f"Timestamp: {data['timestamp']}")
```

## Desarrollo

### Hot Reload (Requiere air)

Instalar Air:
```bash
go install github.com/cosmtrek/air@latest
```

Ejecutar con hot reload:
```bash
make dev
# o
air
```

### Formatear código
```bash
make fmt
```

### Ejecutar tests
```bash
make test
```

### Ver cobertura
```bash
make test-coverage
# Abre coverage.html en el navegador
```

## Comandos Útiles

### Ver logs estructurados
Los logs se muestran en formato JSON:
```json
{
  "level": "info",
  "timestamp": "2026-03-01T17:33:58.635-0500",
  "caller": "logger/logger.go:40",
  "msg": "Iniciando aplicación",
  "port": "8080"
}
```

### Apagar gracefully
Presiona `Ctrl+C` en la terminal donde está corriendo la aplicación.

El servidor esperará hasta 30 segundos para completar las peticiones en curso antes de apagar.

## Agregar un Nuevo Endpoint

### 1. Crear Entity (si es necesario)
```go
// internal/domain/user/entity/user.go
package entity

type User struct {
    id    string
    name  string
    email string
}
```

### 2. Definir Repository Interface
```go
// internal/domain/user/repository/user_repository.go
package repository

type UserRepository interface {
    GetByID(ctx context.Context, id string) (*entity.User, error)
}
```

### 3. Crear Use Case
```go
// internal/application/user/usecase/get_user_usecase.go
package usecase

type GetUserUseCase struct {
    userRepo repository.UserRepository
}
```

### 4. Implementar Handler
```go
// internal/infrastructure/http/handler/user_handler.go
package handler

type UserHandler struct {
    getUserUseCase *usecase.GetUserUseCase
}

func (h *UserHandler) GetUser(w http.ResponseWriter, r *http.Request) {
    // implementación
}
```

### 5. Registrar Ruta
```go
// internal/infrastructure/http/router/router.go
r.muxRouter.HandleFunc("/users/{id}", r.userHandler.GetUser).Methods("GET")
```

### 6. Dependency Injection en main.go
```go
userRepo := repository.NewUserRepository()
getUserUseCase := usecase.NewGetUserUseCase(userRepo)
userHandler := handler.NewUserHandler(getUserUseCase)
router := router.NewRouter(pingHandler, userHandler)
```
