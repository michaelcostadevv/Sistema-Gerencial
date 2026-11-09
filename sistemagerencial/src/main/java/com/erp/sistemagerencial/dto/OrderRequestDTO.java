package com.erp.sistemagerencial.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// Repare que o pedido NÃO recebe preço nem total: o servidor calcula a partir do banco.
// Se aceitássemos o preço do cliente, qualquer um poderia comprar por R$ 0,01.
public record OrderRequestDTO(

        @NotNull(message = "O ID do cliente é obrigatório")
        Long clientId,

        // @Valid aqui faz o Spring validar também cada ItemRequestDTO dentro da lista.
        @NotEmpty(message = "O pedido deve conter pelo menos um item")
        List<@Valid @NotNull ItemRequestDTO> items
) {
}
