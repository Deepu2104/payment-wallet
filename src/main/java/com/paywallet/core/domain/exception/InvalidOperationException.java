package com.paywallet.core.domain.exception;

public class InvalidOperationException extends BusinessException {
    public InvalidOperationException(String message) {
        super(message, "INVALID_OPERATION");
    }

    public InvalidOperationException(String message, String errorCode) {
        super(message, errorCode);
    }
}
