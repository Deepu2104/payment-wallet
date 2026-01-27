package com.paywallet.core.domain.exception;

public class IdempotencyException extends BusinessException {
    public IdempotencyException(String message) {
        super(message, "IDEMPOTENCY_CONFLICT");
    }
}
