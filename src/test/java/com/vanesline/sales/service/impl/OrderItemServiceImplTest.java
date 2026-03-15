package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.orderitem.OrderItemRequest;
import com.vanesline.sales.dto.orderitem.OrderItemResponse;
import com.vanesline.sales.dto.orderitem.OrderItemSearchCriteria;
import com.vanesline.sales.entity.Order;
import com.vanesline.sales.entity.OrderItem;
import com.vanesline.sales.repository.OrderItemRepository;
import com.vanesline.sales.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem orderItem;
    private OrderItemRequest request;
    private Order order;
    private UUID orderItemId;
    private UUID orderId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        orderItemId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();

        orderItem = OrderItem.builder()
                .productId(productId)
                .quantity(2)
                .orderId(orderId)
                .unitPrice(BigDecimal.valueOf(50))
                .totalPrice(BigDecimal.valueOf(100))
                .build();
        orderItem.setId(orderItemId);

        request = OrderItemRequest.builder()
                .productId(productId)
                .quantity(2)
                .orderId(orderId)
                .unitPrice(BigDecimal.valueOf(50))
                .build();

        order = Order.builder().price(BigDecimal.ZERO).build();
        order.setId(orderId);
    }

    @Test
    void create_Success() {
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        OrderItemResponse response = orderItemService.create(request);

        assertNotNull(response);
        assertEquals(orderItemId, response.getId());
        assertEquals(BigDecimal.valueOf(100), response.getTotalPrice());
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void create_ThrowsEntityNotFoundException_WhenOrderNotFound() {
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderItemService.create(request));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void update_Success() {
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderItemResponse response = orderItemService.update(orderItemId, request);

        assertNotNull(response);
        assertEquals(orderItemId, response.getId());
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getById_Success() {
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));

        OrderItemResponse response = orderItemService.getById(orderItemId);

        assertNotNull(response);
        assertEquals(orderItemId, response.getId());
    }

    @Test
    void getByOrderId_Success() {
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));

        List<OrderItemResponse> responses = orderItemService.getByOrderId(orderId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(orderId, responses.get(0).getOrderId());
    }

    @Test
    void delete_Success() {
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderItemService.delete(orderItemId));

        verify(orderItemRepository).deleteById(orderItemId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deleteByOrderId_Success() {
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderItemService.deleteByOrderId(orderId));

        verify(orderItemRepository).deleteByOrderId(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void search_Success() {
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderItem> page = new PageImpl<>(List.of(orderItem), pageable, 1);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PageResponse<OrderItemResponse> response = orderItemService.search(criteria, pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(orderItem.getId(), response.getContent().get(0).getId());
    }
}
