# 🎨 Diagrama de Arquitectura Visual

## Flujo de una Petición HTTP

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT                                   │
│                    (Browser, curl, etc)                          │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ HTTP GET /ping
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                           │
│                                                                   │
│  ┌──────────────┐      ┌──────────────────┐                     │
│  │    Router    │─────▶│  PingHandler     │                     │
│  │  (Gorilla)   │      │  - Handle()      │                     │
│  └──────────────┘      └─────────┬────────┘                     │
│                                   │                              │
└───────────────────────────────────┼──────────────────────────────┘
                                    │
                                    │ Execute()
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                              │
│                                                                   │
│              ┌──────────────────────┐                            │
│              │   PingUseCase        │                            │
│              │   - Execute()        │                            │
│              └──────────┬───────────┘                            │
│                         │                                        │
└─────────────────────────┼────────────────────────────────────────┘
                          │
                          │ GetHealth()
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                                 │
│                                                                   │
│  ┌───────────────────┐         ┌──────────────────┐             │
│  │ HealthRepository  │◀────────│  Health Entity   │             │
│  │   (Interface)     │         │  - Status()      │             │
│  └─────────┬─────────┘         │  - Message()     │             │
│            │                   │  - Timestamp()   │             │
│            │                   └──────────────────┘             │
└────────────┼────────────────────────────────────────────────────┘
             │
             │ Implements
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                           │
│                                                                   │
│      ┌────────────────────────────────┐                          │
│      │ InMemoryHealthRepository       │                          │
│      │ - GetHealth()                  │                          │
│      │   returns: Health("pong")      │                          │
│      └────────────────────────────────┘                          │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Dependencias entre Capas

```
┌───────────────────────────────────────────────────────────────┐
│                                                                 │
│                        DOMAIN                                   │
│                    (Business Logic)                             │
│                                                                 │
│  • No dependencies                                              │
│  • Pure business rules                                          │
│  • Entities, Repositories (interfaces)                          │
│                                                                 │
└─────────────────────────▲───────────────────────────────────────┘
                          │
                          │ depends on
                          │
┌─────────────────────────┴─────────────────────────────────────┐
│                                                                 │
│                      APPLICATION                                │
│                      (Use Cases)                                │
│                                                                 │
│  • Depends on: Domain                                           │
│  • Orchestrates business logic                                  │
│  • Use repository interfaces                                    │
│                                                                 │
└─────────────────────────▲───────────────────────────────────────┘
                          │
                          │ depends on
                          │
┌─────────────────────────┴─────────────────────────────────────┐
│                                                                 │
│                    INFRASTRUCTURE                               │
│                  (Frameworks & Drivers)                         │
│                                                                 │
│  • Depends on: Domain + Application                             │
│  • Implements domain interfaces                                 │
│  • HTTP handlers, DB, Config, etc                               │
│                                                                 │
└───────────────────────────────────────────────────────────────┘
```

## Estructura de Archivos por Capa

```
┌──────────────────────────────────────────────────────────────────┐
│ DOMAIN LAYER                                                      │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  internal/domain/health/                                          │
│  ├── entity/                                                      │
│  │   └── health.go          ← Entity: Health                     │
│  └── repository/                                                  │
│      └── health_repository.go ← Interface: HealthRepository      │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ APPLICATION LAYER                                                 │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  internal/application/health/                                     │
│  └── usecase/                                                     │
│      └── ping_usecase.go     ← Use Case: Ping                    │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ INFRASTRUCTURE LAYER                                              │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  internal/infrastructure/                                         │
│  ├── config/                                                      │
│  │   └── config.go          ← Configuration                      │
│  ├── http/                                                        │
│  │   ├── handler/                                                 │
│  │   │   └── ping_handler.go ← HTTP Handler                      │
│  │   └── router/                                                  │
│  │       └── router.go       ← Router setup                      │
│  └── repository/                                                  │
│      └── health_repository_impl.go ← Repository Implementation   │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ SHARED / UTILITIES                                                │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  pkg/                                                             │
│  └── logger/                                                      │
│      └── logger.go           ← Structured Logger                 │
│                                                                   │
│  main.go                     ← Entry Point + DI                  │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

## Dependency Injection en main.go

```
main()
  │
  ├─► Logger
  │     └─► NewZapLogger()
  │
  ├─► Config
  │     └─► LoadConfig()
  │
  ├─► Repository
  │     └─► NewInMemoryHealthRepository()
  │           │
  │           └─► implements HealthRepository interface
  │
  ├─► Use Case
  │     └─► NewPingUseCase(healthRepo)
  │           │
  │           └─► depends on HealthRepository interface
  │
  ├─► Handler
  │     └─► NewPingHandler(pingUseCase)
  │           │
  │           └─► depends on PingUseCase
  │
  ├─► Router
  │     └─► NewRouter(pingHandler)
  │           │
  │           └─► registers routes
  │
  └─► HTTP Server
        └─► ListenAndServe(router)
```

## Clean Architecture Principles

```
┌─────────────────────────────────────────────────────────────┐
│                         OUTER LAYER                          │
│                    (Infrastructure)                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                    MIDDLE LAYER                        │  │
│  │                  (Application)                         │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │              INNER LAYER                         │  │  │
│  │  │              (Domain)                            │  │  │
│  │  │                                                  │  │  │
│  │  │  • Entities                                      │  │  │
│  │  │  • Value Objects                                 │  │  │
│  │  │  • Repository Interfaces                         │  │  │
│  │  │  • Domain Services                               │  │  │
│  │  │                                                  │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │                                                         │  │
│  │  • Use Cases                                            │  │
│  │  • Business Logic                                       │  │
│  │  • Orchestration                                        │  │
│  │                                                         │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                               │
│  • HTTP Handlers                                              │
│  • Database Access                                            │
│  • External APIs                                              │
│  • Frameworks                                                 │
│                                                               │
└───────────────────────────────────────────────────────────────┘

RULE: Dependencies point INWARD only (→)
```

## Bounded Context: Health

```
┌─────────────────────────────────────────────────────────────┐
│                   HEALTH BOUNDED CONTEXT                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Entity:      Health                                         │
│  ├─ Status:     string                                       │
│  ├─ Message:    string                                       │
│  └─ Timestamp:  time.Time                                    │
│                                                              │
│  Repository:  HealthRepository                               │
│  └─ GetHealth(ctx) → Health                                  │
│                                                              │
│  Use Case:    PingUseCase                                    │
│  └─ Execute(ctx) → Health                                    │
│                                                              │
│  Handler:     PingHandler                                    │
│  └─ Handle(w, r) → JSON Response                             │
│                                                              │
│  Endpoint:    GET /ping                                      │
│  Response:    {"status":"healthy","message":"pong"}          │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## Technology Stack

```
┌──────────────────────────────────────────────────────────────┐
│                     TECHNOLOGY STACK                          │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  Language:        Go 1.23                                     │
│                                                               │
│  HTTP Router:     Gorilla Mux v1.8.1                          │
│  ├─ Fast routing                                              │
│  ├─ URL parameters                                            │
│  └─ Middleware support                                        │
│                                                               │
│  Logger:          Uber Zap v1.27.1                            │
│  ├─ Structured logging                                        │
│  ├─ High performance                                          │
│  └─ JSON output                                               │
│                                                               │
│  Architecture:    Clean Architecture                          │
│  Design:          Domain-Driven Design (DDD)                  │
│  DI:              Manual (Constructor Injection)              │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

---

**Leyenda**:
- `→` : Flujo de datos / llamadas
- `◀──` : Implementa / depende de
- `│` : Separación de capas
- `▼` : Dirección del flujo
