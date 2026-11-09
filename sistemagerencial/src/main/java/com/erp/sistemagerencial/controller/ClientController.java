package com.erp.sistemagerencial.controller;

import com.erp.sistemagerencial.dto.ClientRequestDTO;
import com.erp.sistemagerencial.dto.ClientResponseDTO;
import com.erp.sistemagerencial.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// O Controller só cuida de HTTP: recebe a requisição, valida o formato (@Valid) e delega ao Service.
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientResponseDTO> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public ClientResponseDTO findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientRequestDTO dto) {
        ClientResponseDTO created = clientService.create(dto);
        // 201 Created + header "Location: /api/clients/{id}" apontando para o recurso criado (padrão REST).
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }
}
