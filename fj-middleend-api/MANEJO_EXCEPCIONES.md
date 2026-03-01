# Sistema de Manejo de Excepciones - Micronaut

## Estructura Creada

```
api/rest/exception/
├── ApplicationException.java         (Excepción base)
├── ErrorResponse.java                (DTO de respuesta de error)
├── handler/
│   ├── ApplicationExceptionHandler.java   (Maneja ApplicationException)
│   └── GlobalExceptionHandler.java        (Maneja todas las excepciones)
└── specific/
    ├── BadRequestException.java      (400)
    ├── NotFoundException.java        (404)
    ├── ConflictException.java        (409)
    └── UnauthorizedException.java    (401)
```

## Uso

### 1. Usar excepciones predefinidas

```java
@Singleton
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    
    @Override
    public Game findById(String id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Game", id));
    }
}
```

### 2. Crear excepción personalizada con status específico

```java
throw new ApplicationException("Custom error message", 422);
```

### 3. Crear excepción por defecto (500)

```java
throw new ApplicationException("Something went wrong");
```

### 4. Excepción con código de error personalizado

```java
throw new ApplicationException("Invalid game state", 400, "INVALID_GAME_STATE");
```

## Respuesta JSON

Todas las excepciones retornan este formato:

```json
{
  "timestamp": "2026-03-01T10:30:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Game with id 'abc123' not found",
  "path": "/api/games/abc123"
}
```

## Excepciones Disponibles

| Excepción | Status | Uso |
|-----------|--------|-----|
| `ApplicationException` | 500 (default) | Base, personalizable |
| `BadRequestException` | 400 | Solicitud inválida |
| `UnauthorizedException` | 401 | Sin autenticación |
| `NotFoundException` | 404 | Recurso no encontrado |
| `ConflictException` | 409 | Conflicto (duplicados) |

## Crear Nueva Excepción Específica

### Ejemplo 1: Excepción de Use Case
```java
package com.acacia.app.use_cases.exceptions.gameUseCase;

import com.acacia.api.rest.exception.ApplicationException;

public class CreateGameException extends ApplicationException {
    
    public CreateGameException(String message) {
        super(message, 422, "CREATE_GAME_FAILED");
    }
    
    public CreateGameException(String message, Throwable cause) {
        super(message, 422, cause);
    }
}
```

### Ejemplo 2: Excepción con código personalizado
```java
package com.acacia.api.rest.exception.specific;

import com.acacia.api.rest.exception.ApplicationException;

public class GameOverException extends ApplicationException {
    
    public GameOverException(String message) {
        super(message, 422, "GAME_OVER");
    }
}
```

### Ejemplo 3: Usar el default (500)
```java
public class InternalGameException extends ApplicationException {
    
    public InternalGameException(String message) {
        super(message);  // Automáticamente 500 + "INTERNAL_SERVER_ERROR"
    }
}
```

## Handlers

### ApplicationExceptionHandler
Captura `ApplicationException` y sus subclases. Respeta el statusCode definido.

### GlobalExceptionHandler
Captura todas las demás excepciones no manejadas. Siempre retorna 500.

## Logging

Todas las excepciones se loguean automáticamente:
- `ApplicationException`: Log con nivel ERROR
- Otras excepciones: Log con nivel ERROR + stacktrace completo
