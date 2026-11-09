package com.erp.sistemagerencial.exception;

// Lançada quando o dado conflita com algo que já existe (ex.: CPF/CNPJ duplicado). Vira HTTP 409.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
