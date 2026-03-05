# Cliente REST - Graph API

## Estructura Creada

### DTOs (Domain Layer)
```
app/domain/dto/graph/
├── NextMoveRequest.java
├── NextMoveOptions.java
├── NextMoveResponse.java
├── NextMove.java
├── NextMoveMeta.java
└── GraphErrorResponse.java
```

### Servicio (Domain Layer)
```
app/domain/services/external/graph/
└── GraphService.java
```

### Cliente e Implementación (Infrastructure Layer)
```
infrastructure/
├── client/graph/
│   └── GraphApiClient.java
└── service/graph/
    ├── GraphServiceImpl.java
    └── GraphApiException.java
```

## Configuración

### Variables de Entorno

```bash
export GRAPH_API_URL=http://localhost:8081
```

### application.properties

```properties
micronaut.http.services.graph-api.url=${GRAPH_API_URL:`http://localhost:8081`}
micronaut.http.services.graph-api.read-timeout=30s
micronaut.http.services.graph-api.connect-timeout=10s
```

## Uso

### En un Use Case

```java
@Singleton
@RequiredArgsConstructor
public class GetBestMoveUseCase {
    
    private final GraphService graphService;
    
    public NextMoveResponse getBestMove(List<Integer> currentState) {
        NextMoveRequest request = new NextMoveRequest(
            currentState,
            "default",
            new NextMoveOptions(true, true)
        );
        
        return graphService.getNextMove(request);
    }
}
```

### Respuestas

#### Éxito (found=true)
```json
{
  "found": true,
  "level": 3,
  "next_move": {
    "type": "STEP",
    "frog_id": 3,
    "from_index": 2,
    "to_index": 3
  },
  "next_state": [1, 2, 0, 3, 4, 5, 6],
  "meta": {
    "goal_state": [4, 5, 6, 0, 1, 2, 3],
    "predicted_remaining_cost": 14,
    "legal_moves_from_state": 2,
    "strategy": "POLICY_TABLE",
    "policy_version": "level_3_v1",
    "load_source": "EMBEDDED",
    "time_ms": 1
  }
}
```

#### Sin solución (found=false)
```json
{
  "found": false,
  "level": 5,
  "reason": "NOT_REACHABLE",
  "meta": {
    "strategy": "POLICY_TABLE",
    "policy_version": "level_5_v1"
  }
}
```

## Manejo de Errores

### GraphApiException

Se lanza cuando:
- La API retorna error (400, 404, 500, etc.)
- Hay problema de conexión
- Timeout

```java
try {
    NextMoveResponse response = graphService.getNextMove(request);
    if (!response.found()) {
        log.warn("No solution found: {}", response.reason());
    }
} catch (GraphApiException e) {
    log.error("Graph API error: {}", e.getMessage());
}
```

### Códigos de Error

- **Status**: 503
- **Error Code**: GRAPH_API_ERROR

## Testing

### Mock del Cliente

```java
@MicronautTest
class MyUseCaseTest {
    
    @Inject
    MyUseCase useCase;
    
    @Inject
    GraphService graphService;
    
    @MockBean(GraphService.class)
    GraphService graphService() {
        return mock(GraphService.class);
    }
    
    @Test
    void shouldGetNextMove() {
        NextMoveResponse mockResponse = new NextMoveResponse(
            true,
            3,
            new NextMove("STEP", 3, 2, 3),
            List.of(1, 2, 0, 3, 4, 5, 6),
            null,
            null
        );
        
        when(graphService.getNextMove(any()))
            .thenReturn(mockResponse);
            
        // test...
    }
}
```

## Extensión para Nuevos Endpoints

### 1. Agregar método al cliente

```java
@Client(id = "graph-api")
public interface GraphApiClient {
    
    @Post("/v1/graph/next-move")
    NextMoveResponse getNextMove(@Body NextMoveRequest request);
    
    @Get("/v1/graph/validate")
    ValidateResponse validateState(@QueryValue List<Integer> state);
}
```

### 2. Agregar método al servicio

```java
public interface GraphService {
    NextMoveResponse getNextMove(NextMoveRequest request);
    ValidateResponse validateState(List<Integer> state);
}
```

### 3. Implementar en GraphServiceImpl

```java
@Override
public ValidateResponse validateState(List<Integer> state) {
    try {
        return graphApiClient.validateState(state);
    } catch (HttpClientResponseException e) {
        throw new GraphApiException("Failed to validate state", e);
    }
}
```

## Configuración Avanzada

### Timeouts personalizados por endpoint

```properties
micronaut.http.services.graph-api.paths[0].pattern=/v1/graph/next-move
micronaut.http.services.graph-api.paths[0].read-timeout=60s

micronaut.http.services.graph-api.paths[1].pattern=/v1/graph/**
micronaut.http.services.graph-api.paths[1].read-timeout=30s
```

### Retry automático

```properties
micronaut.http.services.graph-api.retry-attempts=3
micronaut.http.services.graph-api.retry-delay=1s
```

### Circuit Breaker

Agrega al `build.gradle.kts`:
```kotlin
implementation("io.micronaut:micronaut-retry")
```

Y anota el cliente:
```java
@Client(id = "graph-api")
@Retryable(attempts = "3", delay = "1s")
public interface GraphApiClient {
    // ...
}
```
