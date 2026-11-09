package com.erp.sistemagerencial.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// A tabela se chama "tb_orders" porque ORDER é palavra reservada do SQL (ORDER BY).
@Entity
@Table(name = "tb_orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    // Vários pedidos podem pertencer a um único cliente.
    // LAZY: o cliente só é buscado no banco quando for realmente acessado.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Um pedido tem vários itens. CascadeType.ALL: salvar o pedido salva os itens junto.
    // orphanRemoval: item removido da lista é apagado do banco.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItem> items = new ArrayList<>();

    // BigDecimal para dinheiro: Double tem erro de arredondamento (0.1 + 0.2 = 0.30000000000000004).
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    public void addItem(OrderItem item) {
        items.add(item);
    }

    // O total é sempre derivado dos itens, nunca vem de fora — assim não fica inconsistente.
    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
