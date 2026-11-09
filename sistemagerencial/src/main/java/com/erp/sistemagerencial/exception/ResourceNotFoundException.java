package com.erp.sistemagerencial.exception;

// Lançada quando um registro buscado por ID não existe. Vira HTTP 404.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " não encontrado com o ID: " + id);
    }
}
