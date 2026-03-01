# Arquitectura del Proyecto

## Principios de Clean Architecture

Este proyecto sigue los principios de Clean Architecture propuestos por Robert C. Martin, organizando el código en capas concéntricas donde las dependencias apuntan hacia adentro.

### Capas

```
┌─────────────────────────────────────────┐
│         Infrastructure Layer            │  <- Frameworks, Drivers, Web
│  (HTTP Handlers, Repositories Impl)     │
├─────────────────────────────────────────┤
│         Application Layer               │  <- Use Cases
│        (Business Logic)                 │
├─────────────────────────────────────────┤
│           Domain Layer                  │  <- Entities, Value Objects
│      (Enterprise Business Rules)        │
└─────────────────────────────────────────┘
```

## Domain-Driven Design (DDD)

### Conceptos implementados

1. **Entities**: Objetos con identidad única que persiste en el tiempo
   - Ubicación: `internal/domain/*/entity/`
   - Ejemplo: `Health` entity

2. **Value Objects**: Objetos inmutables sin identidad, definidos por sus atributos
   - Se pueden agregar en: `internal/domain/*/valueobject/`

3. **Repositories**: Interfaces que definen el contrato para acceso a datos
   - Interfaces: `internal/domain/*/repository/`
   - Implementaciones: `internal/infrastructure/repository/`

4. **Use Cases**: Casos de uso que encapsulan la lógica de negocio
   - Ubicación: `internal/application/*/usecase/`

5. **Aggregates**: Grupos de entities y value objects tratados como unidad
   - Se pueden agregar en: `internal/domain/*/aggregate/`

## Flujo de una Petición

```
HTTP Request
    ↓
[Router] → routes traffic
    ↓
[Handler] → handles HTTP concerns (serialization, status codes)
    ↓
[Use Case] → executes business logic
    ↓
[Repository Interface] → defines data contract
    ↓
[Repository Implementation] → accesses data source
    ↓
[Domain Entity] → returns business object
    ↓
Response flows back up the chain
```

## Dependency Injection

La inyección de dependencias se realiza manualmente en `main.go`:

```go
// 1. Crear instancias de bajo nivel (repositorios)
healthRepo := repository.NewInMemoryHealthRepository()

// 2. Inyectar en casos de uso
pingUseCase := usecase.NewPingUseCase(healthRepo)

// 3. Inyectar en handlers
pingHandler := handler.NewPingHandler(pingUseCase)

// 4. Configurar router
router := router.NewRouter(pingHandler)
```

## Reglas de Dependencia

1. **Domain Layer** (internal/domain/)
   - NO puede depender de ninguna otra capa
   - Contiene interfaces puras y lógica de negocio
   - Sin dependencias externas

2. **Application Layer** (internal/application/)
   - Puede depender de Domain Layer
   - NO puede depender de Infrastructure Layer
   - Usa interfaces del dominio

3. **Infrastructure Layer** (internal/infrastructure/)
   - Puede depender de Domain y Application
   - Implementa interfaces definidas en Domain
   - Contiene frameworks y herramientas externas

## Agregar un Nuevo Bounded Context

Para agregar un nuevo contexto (ej: "users"):

1. Crear estructura de dominio:
```
internal/domain/users/
├── entity/
│   └── user.go
├── valueobject/
│   └── email.go
└── repository/
    └── user_repository.go
```

2. Crear casos de uso:
```
internal/application/users/
└── usecase/
    ├── create_user_usecase.go
    └── get_user_usecase.go
```

3. Implementar infraestructura:
```
internal/infrastructure/
├── repository/
│   └── user_repository_impl.go
└── http/
    └── handler/
        └── user_handler.go
```

4. Registrar en el router y hacer DI en main.go

## Testing Strategy

- **Unit Tests**: Para entidades y value objects del dominio
- **Integration Tests**: Para use cases con mocks de repositorios
- **E2E Tests**: Para handlers HTTP completos

```bash
# Ejecutar tests
make test

# Con cobertura
make test-coverage
```

## Mejores Prácticas

1. ✅ Las entidades deben tener métodos para cambiar su estado
2. ✅ Los repositorios solo devuelven y persisten entities/aggregates
3. ✅ Los use cases orquestan múltiples operaciones si es necesario
4. ✅ Los handlers no contienen lógica de negocio
5. ✅ Las interfaces se definen donde se usan (dominio/aplicación)
6. ✅ Las implementaciones van en infraestructura
7. ✅ Sin dependencias circulares entre capas
