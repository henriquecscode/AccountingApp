
Request → Controller → @Aspect wraps → Service → Repository → Database

# Exception handling
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceExists(ResourceAlreadyExistsException e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException e) {
        logger.error("Database exception reached controller", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("An error occurred. Please try again later."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Unexpected exception", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("An unexpected error occurred"));
    }
}
```

## Complete Flow Example
```
1. User makes request → POST /api/auth/signup

2. Controller receives request
   ↓
3. Controller calls service.signup()
   ↓
4. @Aspect intercepts service method
   ↓
5. Service executes → calls repository.save()
   ↓
6. Database throws DuplicateKeyException
   ↓
7. @Aspect catches it → throws ResourceAlreadyExistsException
   ↓
8. Exception bubbles up to controller
   ↓
9. @RestControllerAdvice catches ResourceAlreadyExistsException
   ↓
10. Returns HTTP 409 Conflict with JSON error message
```

## When to Use What

### Use @Aspect when you want to:
- Handle exceptions at the service/business logic layer
- Transform technical exceptions into business exceptions
- Add cross-cutting concerns (logging, metrics, security)
- Keep services clean without try-catch blocks
- Handle exceptions from multiple layers (not just controllers)

### Use @RestControllerAdvice when you want to:
- Convert exceptions to HTTP responses
- Centralize HTTP error response formatting
- Handle controller-specific exceptions
- Return consistent error response structure
- Set HTTP status codes and headers

## My Recommendation for Your Project

Use **both**:

1. **@Aspect** - Transform `DataAccessException` → `DatabaseException` at service layer
2. **@RestControllerAdvice** - Convert `DatabaseException` → HTTP 500 response at controller layer

This separation gives you:
- Clean service code (no try-catch)
- Clear exception transformation logic
- Consistent HTTP error responses
- Easy to test and maintain
```
Database Error (DataAccessException)
        ↓
    @Aspect transforms
        ↓
Business Exception (DatabaseException)
        ↓
    @RestControllerAdvice converts
        ↓
HTTP Response (500 Internal Server Error)   