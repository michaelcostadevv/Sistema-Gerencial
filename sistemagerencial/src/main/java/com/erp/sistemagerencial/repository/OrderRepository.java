package com.erp.sistemagerencial.repository;

import com.erp.sistemagerencial.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // @EntityGraph faz o Hibernate trazer pedido + cliente + itens + produtos numa única query com JOIN.
    // Sem isso teríamos o problema "N+1": 1 query para os pedidos e mais N queries (uma por pedido)
    // para buscar cliente/itens/produtos.
    @EntityGraph(attributePaths = {"client", "items", "items.product"})
    @Query("select o from Order o order by o.orderDate desc")
    List<Order> findAllWithDetails();

    @EntityGraph(attributePaths = {"client", "items", "items.product"})
    Optional<Order> findWithDetailsById(Long id);
}
