# Estructura del Proyecto

```
graph-api/
│
├── cmd/                              # Puntos de entrada de la aplicación
│   └── api/                          # (Reservado para futuras apps)
│
├── internal/                         # Código privado de la aplicación
│   │
│   ├── domain/                       # CAPA DE DOMINIO
│   │   └── health/                   # Bounded Context: Health
│   │       ├── entity/               # Entidades del dominio
│   │       │   └── health.go         # Entity: Health
│   │       └── repository/           # Interfaces de repositorios
│   │           └── health_repository.go
│   │
│   ├── application/                  # CAPA DE APLICACIÓN
│   │   └── health/                   # Casos de uso de Health
│   │       └── usecase/
│   │           └── ping_usecase.go   # Use Case: Ping
│   │
│   └── infrastructure/               # CAPA DE INFRAESTRUCTURA
│       ├── config/                   # Configuración de la app
│       │   └── config.go
│       ├── http/                     # Adaptadores HTTP
│       │   ├── handler/              # HTTP Handlers
│       │   │   └── ping_handler.go
│       │   └── router/               # Enrutamiento
│       │       └── router.go
│       └── repository/               # Implementaciones de repositorios
│           └── health_repository_impl.go
│
├── pkg/                              # Código reutilizable (público)
│   └── logger/                       # Logger estructurado
│       └── logger.go
│
├── bin/                              # Binarios compilados
│   └── api.exe
│
├── .env.example                      # Variables de entorno ejemplo
├── .gitignore                        # Archivos ignorados por Git
├── ARCHITECTURE.md                   # Documentación de arquitectura
├── EXAMPLES.md                       # Ejemplos de uso
├── go.mod                            # Dependencias de Go
├── go.sum                            # Checksums de dependencias
├── main.go                           # Punto de entrada principal
├── Makefile                          # Comandos útiles
└── README.md                         # Documentación principal
```

## Descripción de Directorios

### `/cmd`
Contiene los puntos de entrada de la aplicación. Para proyectos con múltiples aplicaciones (API, CLI, workers), cada una tendría su propia carpeta aquí.

### `/internal`
Código privado que no puede ser importado por otros proyectos. Aquí reside toda la lógica de la aplicación.

#### `/internal/domain`
**Capa de Dominio** - El corazón de la aplicación
- ✅ Sin dependencias externas
- ✅ Lógica de negocio pura
- ✅ Entidades, Value Objects, Interfaces de Repositorios
- ❌ No depende de frameworks
- ❌ No tiene referencias a HTTP, DB, etc.

#### `/internal/application`
**Capa de Aplicación** - Casos de uso
- ✅ Orquesta la lógica de dominio
- ✅ Implementa casos de uso específicos
- ✅ Usa interfaces del dominio
- ❌ No conoce detalles de infraestructura

#### `/internal/infrastructure`
**Capa de Infraestructura** - Implementaciones concretas
- ✅ Implementa interfaces del dominio
- ✅ Maneja frameworks y librerías externas
- ✅ HTTP handlers, DB access, Config, etc.
- ✅ Adaptadores al mundo exterior

### `/pkg`
Código que puede ser importado por otros proyectos. Utilidades generales como loggers, helpers, etc.

### `/bin`
Directorio para binarios compilados (no se sube a Git).

## Bounded Contexts

Actualmente el proyecto tiene un bounded context:

### Health Context
**Propósito**: Verificar el estado de salud del sistema

**Componentes**:
- **Entity**: `Health` - Estado de salud del sistema
- **Repository**: `HealthRepository` - Interface para obtener estado
- **Use Case**: `PingUseCase` - Caso de uso para ping/pong
- **Handler**: `PingHandler` - Maneja peticiones HTTP `/ping`
- **Implementation**: `InMemoryHealthRepository` - Implementación en memoria

**Flujo**:
```
GET /ping 
  → PingHandler 
  → PingUseCase 
  → HealthRepository 
  → Health Entity 
  → JSON Response
```

## Agregar un Nuevo Bounded Context

Ejemplo para un contexto "Users":

```
internal/
├── domain/
│   └── users/
│       ├── entity/
│       │   └── user.go
│       ├── valueobject/
│       │   └── email.go
│       └── repository/
│           └── user_repository.go
├── application/
│   └── users/
│       └── usecase/
│           ├── create_user_usecase.go
│           └── get_user_usecase.go
└── infrastructure/
    ├── repository/
    │   └── user_repository_impl.go
    └── http/
        └── handler/
            └── user_handler.go
```

## Convenciones de Nomenclatura

- **Packages**: minúsculas, singular (`entity`, `usecase`, `handler`)
- **Files**: snake_case (`ping_handler.go`, `health_repository.go`)
- **Types**: PascalCase (`PingHandler`, `HealthRepository`)
- **Interfaces**: PascalCase terminadas en comportamiento (`HealthRepository`, `Logger`)
- **Constructors**: `New*` (`NewPingHandler`, `NewHealthRepository`)

## Flujo de Dependencias

```
Infrastructure → Application → Domain
        ↓              ↓           ↓
   Implements     Uses APIs    Defines APIs
```

**Regla de Oro**: Las dependencias siempre apuntan hacia adentro (hacia el dominio).
