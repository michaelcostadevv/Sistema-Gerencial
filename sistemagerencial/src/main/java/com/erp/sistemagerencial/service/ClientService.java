package com.erp.sistemagerencial.service;

import com.erp.sistemagerencial.dto.ClientRequestDTO;
import com.erp.sistemagerencial.dto.ClientResponseDTO;
import com.erp.sistemagerencial.entity.Client;
import com.erp.sistemagerencial.exception.ConflictException;
import com.erp.sistemagerencial.exception.ResourceNotFoundException;
import com.erp.sistemagerencial.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    // Injeção por construtor (em vez de @Autowired no campo):
    // - o campo pode ser "final" (não muda depois de criado);
    // - deixa as dependências explícitas;
    // - nos testes dá pra passar um mock direto no construtor, sem subir o Spring.
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // readOnly = true: avisa o Hibernate que não haverá escrita, o que permite otimizações.
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll().stream().map(ClientResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO findById(Long id) {
        return ClientResponseDTO.from(getEntity(id));
    }

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO dto) {
        // Checa antes para dar uma mensagem amigável. A constraint UNIQUE do banco continua sendo
        // a garantia final (se duas requisições chegarem juntas, o GlobalExceptionHandler trata).
        if (clientRepository.existsByCpfCnpj(dto.cpfCnpj())) {
            throw new ConflictException("Já existe um cliente cadastrado com este CPF/CNPJ");
        }

        Client client = new Client();
        client.setName(dto.name());
        client.setCpfCnpj(dto.cpfCnpj());
        client.setEmail(dto.email());
        client.setPhone(dto.phone());

        return ClientResponseDTO.from(clientRepository.save(client));
    }

    // Usado por outros services (ex.: OrderService) que precisam da entidade, não do DTO.
    Client getEntity(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }
}
