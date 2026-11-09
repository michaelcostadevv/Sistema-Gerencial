package com.erp.sistemagerencial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// O que o cliente da API ENVIA para cadastrar um Client.
// Não tem "id": quem gera o id é o banco — isso impede que alguém sobrescreva um registro existente.
public record ClientRequestDTO(

        @NotBlank(message = "O nome do cliente é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String name,

        @NotBlank(message = "O CPF ou CNPJ é obrigatório")
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "O CPF/CNPJ deve conter 11 ou 14 dígitos, apenas números")
        String cpfCnpj,

        @Email(message = "E-mail inválido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String phone
) {
}
