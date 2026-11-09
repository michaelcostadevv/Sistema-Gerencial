package com.erp.sistemagerencial.entity;

import com.erp.sistemagerencial.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_produtos")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    // Controle de concorrência otimista: se duas requisições tentarem baixar o estoque
    // do mesmo produto ao mesmo tempo, a segunda falha em vez de sobrescrever a primeira.
    // @ColumnDefault("0") garante que produtos já cadastrados antes desta coluna existir recebam versão 0.
    @Version
    @ColumnDefault("0")
    @Column(nullable = false)
    private Long version;

    // A regra "não vender sem estoque" mora na entidade, perto do dado que ela protege.
    public void baixarEstoque(int quantidade) {
        if (quantidade > quantidadeEstoque) {
            throw new BusinessException(
                    "Estoque insuficiente para o produto '" + nome + "'. Disponível: "
                            + quantidadeEstoque + ", solicitado: " + quantidade);
        }
        this.quantidadeEstoque -= quantidade;
    }
}
