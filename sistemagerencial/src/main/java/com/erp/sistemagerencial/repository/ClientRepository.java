package com.erp.sistemagerencial.repository;

import com.erp.sistemagerencial.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Spring Data gera a query a partir do nome do método: SELECT count(*) > 0 ... WHERE cpf_cnpj = ?
    boolean existsByCpfCnpj(String cpfCnpj);
}
