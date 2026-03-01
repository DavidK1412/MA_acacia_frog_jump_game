# Graph API - Clean Architecture + DDD

API RESTful construida con Go siguiendo los principios de Clean Architecture y Domain-Driven Design (DDD).

## 📚 Documentación

- **[QUICKSTART.md](QUICKSTART.md)** - Guía rápida para empezar en 5 minutos ⚡
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detalles de arquitectura y principios DDD 🏗️
- **[STRUCTURE.md](STRUCTURE.md)** - Estructura de directorios y convenciones 📁
- **[EXAMPLES.md](EXAMPLES.md)** - Ejemplos de uso y desarrollo 💻
- **[DIAGRAMS.md](DIAGRAMS.md)** - Diagramas visuales de la arquitectura 🎨
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Resumen completo del proyecto 📦

## ✨ Características

✅ Clean Architecture con 3 capas bien definidas  
✅ Domain-Driven Design (DDD)  
✅ Dependency Injection manual  
✅ Logger estructurado (Zap)  
✅ Configuración por variables de entorno  
✅ Graceful shutdown  
✅ Sin dependencias innecesarias  
✅ Código limpio y bien organizado

## 🏗️ Arquitectura

El proyecto está organizado en las siguientes capas:

```
graph-api/
├── cmd/
│   └── api/                    # Puntos de entrada de la aplicación
├── internal/
│   ├── domain/                 # Capa de Dominio (Entidades, Value Objects, Repositorios)
│   │   └── health/
│   │       ├── entity/
│   │       └── repository/
│   ├── application/            # Capa de Aplicación (Use Cases)
│   │   └── health/
│   │       └── usecase/
│   ├── api/                    # Capa de API (HTTP Handlers, Routers)
│   │   ├── handler/
│   │   └── router/
│   └── infrastructure/         # Capa de Infraestructura (Config, Repositories)
│       ├── config/
│       └── repository/
└── pkg/                        # Paquetes compartidos
    └── logger/
```

### Capas

1. **Domain (Dominio)**: Contiene la lógica de negocio pura, entidades, value objects y contratos de repositorios.
2. **Application (Aplicación)**: Contiene los casos de uso que orquestan la lógica de negocio.
3. **API**: Contiene los handlers HTTP y routers que exponen la funcionalidad.
4. **Infrastructure (Infraestructura)**: Contiene implementaciones concretas (repositorios, configuración, etc.).
5. **Pkg**: Utilidades y paquetes compartidos que pueden ser reutilizados.

## 🚀 Inicio Rápido

### Prerrequisitos

- Go 1.23 o superior
- Git (opcional)

### Instalación y Ejecución

```bash
# 1. Instalar dependencias
go mod download

# 2. Ejecutar la aplicación
go run main.go

# 3. Probar el endpoint
curl http://localhost:8080/ping
```

**¡Listo!** La aplicación estará disponible en `http://localhost:8080`

Para más detalles, consulta [QUICKSTART.md](QUICKSTART.md)

## 📡 Endpoints

### Health Check

```
GET /ping
```

Respuesta:
```json
{
  "status": "healthy",
  "message": "pong",
  "timestamp": "2026-03-01T17:30:00Z"
}
```

## ⚙️ Configuración

La aplicación se configura mediante variables de entorno:

- `SERVER_PORT`: Puerto del servidor (por defecto: 8080)

Ejemplo:
```bash
SERVER_PORT=3000 go run main.go
```

## 🧪 Testing

Para ejecutar los tests:
```bash
go test ./...
```

Para ejecutar tests con cobertura:
```bash
go test -cover ./...
```

## 🛠️ Desarrollo

### Comandos útiles

```bash
make run           # Ejecutar aplicación
make build         # Compilar binario
make test          # Ejecutar tests
make test-coverage # Tests con cobertura
make fmt           # Formatear código
make clean         # Limpiar binarios
```

### Agregar un nuevo endpoint

Para agregar un endpoint, sigue estos pasos (ejemplo completo en [EXAMPLES.md](EXAMPLES.md)):

1. Crear la entidad en `internal/domain/<contexto>/entity/`
2. Definir el repositorio en `internal/domain/<contexto>/repository/`
3. Crear el use case en `internal/application/<contexto>/usecase/`
4. Implementar el handler en `internal/api/handler/`
5. Registrar la ruta en `internal/api/router/`
6. Implementar el repositorio en `internal/infrastructure/repository/`
7. Configurar dependency injection en `main.go`

### Principios arquitectónicos

- ✅ **Dependency Inversion**: Las dependencias apuntan hacia adentro (hacia el dominio)
- ✅ **Single Responsibility**: Cada componente tiene una única responsabilidad
- ✅ **Interface Segregation**: Interfaces pequeñas y específicas
- ✅ **Domain-Driven Design**: El dominio es el centro de la aplicación
- ✅ **Separation of Concerns**: Cada capa tiene su responsabilidad clara

Consulta [ARCHITECTURE.md](ARCHITECTURE.md) para más detalles.

## 📦 Dependencias

- [gorilla/mux](https://github.com/gorilla/mux): Router HTTP
- [zap](https://github.com/uber-go/zap): Logger estructurado de alto rendimiento

## 📝 Licencia

Este proyecto está bajo la licencia MIT.
