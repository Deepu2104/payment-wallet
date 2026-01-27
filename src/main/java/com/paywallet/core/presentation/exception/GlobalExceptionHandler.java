package com.paywallet.core.presentation.exception;

import com.paywallet.core.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<ErrorResponse> handleIdempotency(IdempotencyException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperation(InvalidOperationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        "VALIDATION_ERROR",
                        LocalDateTime.now().toString(),
                        errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "INTERNAL_ERROR");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String errorCode) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                message,
                errorCode,
                LocalDateTime.now().toString(),
                null);
        return new ResponseEntity<>(response, status);
    }

    record ErrorResponse(int status, String message, String errorCode, String timestamp, Object details) {
    }
}
