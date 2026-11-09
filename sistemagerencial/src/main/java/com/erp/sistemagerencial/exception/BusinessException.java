package com.erp.sistemagerencial.exception;

// Lançada quando uma regra de negócio é violada (ex.: estoque insuficiente). Vira HTTP 422.
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
