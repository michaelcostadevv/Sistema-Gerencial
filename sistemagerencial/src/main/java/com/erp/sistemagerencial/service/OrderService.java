package com.erp.sistemagerencial.service;

import com.erp.sistemagerencial.dto.ItemRequestDTO;
import com.erp.sistemagerencial.dto.OrderRequestDTO;
import com.erp.sistemagerencial.dto.OrderResponseDTO;
import com.erp.sistemagerencial.entity.Client;
import com.erp.sistemagerencial.entity.Order;
import com.erp.sistemagerencial.entity.OrderItem;
import com.erp.sistemagerencial.entity.Product;
import com.erp.sistemagerencial.exception.ResourceNotFoundException;
import com.erp.sistemagerencial.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final Clock clock;

    // Clock injetado em vez de LocalDateTime.now() direto: nos testes dá pra fixar a data.
    public OrderService(OrderRepository orderRepository, ClientService clientService,
                        ProductService productService, Clock clock) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.productService = productService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAllWithDetails().stream().map(OrderResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {
        return orderRepository.findWithDetailsById(id)
                .map(OrderResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    // @Transactional garante o "tudo ou nada": se o 3º item não tiver estoque, a baixa de estoque
    // dos itens 1 e 2 é desfeita (rollback) e o pedido não é salvo.
    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto) {
        Client client = clientService.getEntity(dto.clientId());

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now(clock));

        for (ItemRequestDTO itemDto : dto.items()) {
            Product product = productService.getEntity(itemDto.productId());
            product.baixarEstoque(itemDto.quantity()); // lança BusinessException se faltar estoque
            order.addItem(new OrderItem(product, itemDto.quantity()));
        }

        order.recalculateTotal();

        // Não precisa chamar productRepository.save(product): dentro de uma transação o Hibernate
        // detecta que a entidade mudou ("dirty checking") e gera o UPDATE sozinho no commit.
        return OrderResponseDTO.from(orderRepository.save(order));
    }
}
