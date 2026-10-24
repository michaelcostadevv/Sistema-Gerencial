package com.erp.sistemagerencial.dto;

import com.erp.sistemagerencial.model.Client;

public class ClientResponseDTO {

    private Long id;
    private String name;
    private String cpfCnpj;
    private String email;
    private String phone;

    // Construtor que converte a Entidade do banco para o DTO de resposta
    public ClientResponseDTO(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.cpfCnpj = client.getCpfCnpj();
        this.email = client.getEmail();
        this.phone = client.getPhone();
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCpfCnpj() { return cpfCnpj; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}