package co.com.crediya.cy_authentication.api.config;

import co.com.crediya.cy_authentication.api.dto.ErrorResponse;
import co.com.crediya.cy_authentication.exception.DataPersistenceException;
import co.com.crediya.cy_authentication.exception.InvalidUserDataException;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFoundException(
            UserNotFoundException ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, ex.getMessage(), HttpStatus.NOT_FOUND, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidUserDataException(
            InvalidUserDataException ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, ex.getMessage(), HttpStatus.BAD_REQUEST, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }
    
    @ExceptionHandler(DataPersistenceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataPersistenceException(
            DataPersistenceException ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, "Error en la persistencia de datos: " + ex.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, "Error interno del servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    private ErrorResponse buildErrorResponse(String path, String message, 
                                            HttpStatus status, String traceId) {
        return ErrorResponse.builder()
                .path(path)
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
    }
}