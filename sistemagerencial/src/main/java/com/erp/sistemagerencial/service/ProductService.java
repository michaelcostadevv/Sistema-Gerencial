package com.erp.sistemagerencial.service;

import com.erp.sistemagerencial.dto.ProductRequestDTO;
import com.erp.sistemagerencial.dto.ProductResponseDTO;
import com.erp.sistemagerencial.entity.Product;
import com.erp.sistemagerencial.exception.ResourceNotFoundException;
import com.erp.sistemagerencial.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream().map(ProductResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        return ProductResponseDTO.from(getEntity(id));
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = new Product();
        product.setNome(dto.nome());
        product.setDescricao(dto.descricao());
        product.setPreco(dto.preco());
        product.setQuantidadeEstoque(dto.quantidadeEstoque());

        return ProductResponseDTO.from(productRepository.save(product));
    }

    Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
    }
}
