package com.erp.sistemagerencial.service;

import com.erp.sistemagerencial.dto.ItemRequestDTO;
import com.erp.sistemagerencial.dto.OrderRequestDTO;
import com.erp.sistemagerencial.dto.OrderResponseDTO;
import com.erp.sistemagerencial.entity.Client;
import com.erp.sistemagerencial.entity.Order;
import com.erp.sistemagerencial.entity.Product;
import com.erp.sistemagerencial.exception.BusinessException;
import com.erp.sistemagerencial.exception.ResourceNotFoundException;
import com.erp.sistemagerencial.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Teste unitário do Service: as dependências (repositório, outros services) são "mocks" do Mockito,
// então testamos SÓ a regra de negócio do OrderService, sem banco de dados.
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock ClientService clientService;
    @Mock ProductService productService;

    private final Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
    private OrderService orderService;

    private Client client;
    private Product teclado;
    private Product mouse;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, clientService, productService, clock);

        client = new Client();
        client.setId(1L);
        client.setName("Maria");

        teclado = product(10L, "Teclado", "150.00", 5);
        mouse = product(20L, "Mouse", "49.90", 10);
    }

    private Product product(Long id, String nome, String preco, int estoque) {
        Product p = new Product();
        p.setId(id);
        p.setNome(nome);
        p.setPreco(new BigDecimal(preco));
        p.setQuantidadeEstoque(estoque);
        return p;
    }

    @Test
    void create_calculaTotalBaixaEstoqueESalva() {
        when(clientService.getEntity(1L)).thenReturn(client);
        when(productService.getEntity(10L)).thenReturn(teclado);
        when(productService.getEntity(20L)).thenReturn(mouse);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderRequestDTO dto = new OrderRequestDTO(1L, List.of(
                new ItemRequestDTO(10L, 2),   // 2 x 150.00 = 300.00
                new ItemRequestDTO(20L, 3))); //  3 x 49.90 = 149.70

        OrderResponseDTO result = orderService.create(dto);

        assertThat(result.totalAmount()).isEqualByComparingTo("449.70");
        assertThat(result.items()).hasSize(2);
        assertThat(result.orderDate()).isEqualTo(LocalDateTime.of(2026, 1, 15, 10, 0));
        assertThat(teclado.getQuantidadeEstoque()).isEqualTo(3);
        assertThat(mouse.getQuantidadeEstoque()).isEqualTo(7);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void create_usaPrecoDoBancoNoMomentoDaVenda() {
        when(clientService.getEntity(1L)).thenReturn(client);
        when(productService.getEntity(10L)).thenReturn(teclado);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO result = orderService.create(new OrderRequestDTO(1L, List.of(new ItemRequestDTO(10L, 1))));

        assertThat(result.items().getFirst().unitPrice()).isEqualByComparingTo("150.00");
    }

    @Test
    void create_falhaSemEstoqueENaoSalvaPedido() {
        when(clientService.getEntity(1L)).thenReturn(client);
        when(productService.getEntity(10L)).thenReturn(teclado);

        OrderRequestDTO dto = new OrderRequestDTO(1L, List.of(new ItemRequestDTO(10L, 6))); // estoque é 5

        assertThatThrownBy(() -> orderService.create(dto)).isInstanceOf(BusinessException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_falhaQuandoClienteNaoExiste() {
        when(clientService.getEntity(99L)).thenThrow(new ResourceNotFoundException("Cliente", 99L));

        OrderRequestDTO dto = new OrderRequestDTO(99L, List.of(new ItemRequestDTO(10L, 1)));

        assertThatThrownBy(() -> orderService.create(dto)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(productService, orderRepository);
    }
}
