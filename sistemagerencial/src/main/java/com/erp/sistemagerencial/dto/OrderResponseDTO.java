package com.erp.sistemagerencial.dto;

import com.erp.sistemagerencial.entity.Order;
import com.erp.sistemagerencial.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        LocalDateTime orderDate,
        Long clientId,
        String clientName,
        List<Item> items,
        BigDecimal totalAmount
) {

    public record Item(Long productId, String productName, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {

        static Item from(OrderItem item) {
            return new Item(
                    item.getProduct().getId(),
                    item.getProduct().getNome(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getSubtotal());
        }
    }

    public static OrderResponseDTO from(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderDate(),
                order.getClient().getId(),
                order.getClient().getName(),
                order.getItems().stream().map(Item::from).toList(),
                order.getTotalAmount());
    }
}
