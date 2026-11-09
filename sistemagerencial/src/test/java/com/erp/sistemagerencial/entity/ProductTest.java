package com.erp.sistemagerencial.entity;

import com.erp.sistemagerencial.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Teste unitário puro: sem Spring, sem banco. Roda em milissegundos.
class ProductTest {

    private Product produtoComEstoque(int estoque) {
        Product p = new Product();
        p.setNome("Teclado");
        p.setPreco(new BigDecimal("100.00"));
        p.setQuantidadeEstoque(estoque);
        return p;
    }

    @Test
    void baixarEstoque_diminuiQuantidade() {
        Product p = produtoComEstoque(10);

        p.baixarEstoque(3);

        assertThat(p.getQuantidadeEstoque()).isEqualTo(7);
    }

    @Test
    void baixarEstoque_permiteZerarEstoque() {
        Product p = produtoComEstoque(5);

        p.baixarEstoque(5);

        assertThat(p.getQuantidadeEstoque()).isZero();
    }

    @Test
    void baixarEstoque_falhaQuandoNaoHaEstoqueSuficiente() {
        Product p = produtoComEstoque(2);

        assertThatThrownBy(() -> p.baixarEstoque(3))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Estoque insuficiente");
        assertThat(p.getQuantidadeEstoque()).isEqualTo(2); // não alterou nada
    }
}
