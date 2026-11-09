package com.erp.sistemagerencial.dto;

import java.time.LocalDateTime;
import java.util.Map;

// Formato padrão de TODOS os erros da API — o front-end sempre sabe o que esperar.
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
