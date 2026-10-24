package com.erp.sistemagerencial.repository;

import com.erp.sistemagerencial.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Método customizado para buscar cliente pelo CPF/CNPJ caso a gente precise validar depois
    Optional<Client> findByCpfCnpj(String cpfCnpj);
}

