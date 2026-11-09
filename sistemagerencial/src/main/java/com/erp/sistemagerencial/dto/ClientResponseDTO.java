package com.erp.sistemagerencial.dto;

import com.erp.sistemagerencial.entity.Client;

// O que a API DEVOLVE. Escolhemos exatamente quais campos expor.
public record ClientResponseDTO(Long id, String name, String cpfCnpj, String email, String phone) {

    // Converte a Entidade do banco para o DTO de resposta
    public static ClientResponseDTO from(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getCpfCnpj(),
                client.getEmail(),
                client.getPhone());
    }
}
