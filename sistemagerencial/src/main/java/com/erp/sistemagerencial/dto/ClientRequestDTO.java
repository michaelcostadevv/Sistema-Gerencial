package com.erp.sistemagerencial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClientRequestDTO {

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    private String name;

    @NotBlank(message = "O CPF ou CNPJ é obrigatório")
    @Size(min = 11, max = 14, message = "O CPF/CNPJ deve ter entre 11 e 14 caracteres")
    private String cpfCnpj;

    @Email(message = "E-mail inválido")
    private String email;

    private String phone;

    // Getters e Setters (ou use @Data do Lombok)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}