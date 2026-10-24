package com.erp.sistemagerencial.controller;

import com.erp.sistemagerencial.model.Product;
import com.erp.sistemagerencial.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. Endpoint para Listar Todos os Produtos (GET)
    @GetMapping
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    // 2. Endpoint para Cadastrar um Novo Produto (POST)
    @PostMapping
    public ResponseEntity<Product> criarProduto(@RequestBody Product product) {
        Product produtoSalvo = productRepository.save(product);

        return ResponseEntity.ok(produtoSalvo);
    }
}
