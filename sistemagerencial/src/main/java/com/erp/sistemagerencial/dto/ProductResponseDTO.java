package com.erp.sistemagerencial.dto;

import com.erp.sistemagerencial.entity.Product;

import java.math.BigDecimal;

// Não expõe o campo "version" (detalhe interno de concorrência).
public record ProductResponseDTO(Long id, String nome, String descricao, BigDecimal preco, Integer quantidadeEstoque) {

    public static ProductResponseDTO from(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getNome(),
                product.getDescricao(),
                product.getPreco(),
                product.getQuantidadeEstoque());
    }
}
