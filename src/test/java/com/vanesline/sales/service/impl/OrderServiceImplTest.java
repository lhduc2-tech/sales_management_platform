package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.order.InvoiceExportResponse;
import com.vanesline.sales.dto.order.OrderRequest;
import com.vanesline.sales.dto.order.OrderResponse;
import com.vanesline.sales.dto.order.OrderSearchProjection;
import com.vanesline.sales.dto.order.SearchOrderCriteria;
import com.vanesline.sales.entity.Customer;
import com.vanesline.sales.entity.Order;
import com.vanesline.sales.entity.OrderItem;
import com.vanesline.sales.entity.Product;
import com.vanesline.sales.entity.Shop;
import com.vanesline.sales.repository.CustomerRepository;
import com.vanesline.sales.repository.OrderItemRepository;
import com.vanesline.sales.repository.OrderRepository;
import com.vanesline.sales.repository.ProductRepository;
import com.vanesline.sales.repository.ShopRepository;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderRequest request;
    private UUID orderId;
    private UUID customerId;
    private UUID shopId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        shopId = UUID.randomUUID();

        order = Order.builder()
                .customerId(customerId)
                .orderOrigin("WEB")
                .price(BigDecimal.valueOf(100.0))
                .paymentState("PAID")
                .processState("COMPLETED")
                .shopId(shopId)
                .orderName("ORD-123456")
                .build();
        order.setId(orderId);

        request = OrderRequest.builder()
                .customerId(customerId)
                .orderOrigin("WEB")
                .price(BigDecimal.valueOf(100.0))
                .paymentState("PAID")
                .processState("COMPLETED")
                .shopId(shopId)
                .build();
    }

    @Test
    void create_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer.builder().fullName("Test Customer").build()));

        OrderResponse response = orderService.create(request);

        assertNotNull(response);
        assertEquals(orderId, response.getId());
        assertEquals("ORD-123456", response.getOrderName());
        assertEquals("Test Customer", response.getCustomerName());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void update_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer.builder().fullName("Test Customer").build()));

        OrderResponse response = orderService.update(orderId, request);

        assertNotNull(response);
        assertEquals(orderId, response.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void update_ThrowsEntityNotFoundException_WhenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.update(orderId, request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getById_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer.builder().fullName("Test Customer").build()));

        OrderResponse response = orderService.getById(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.getId());
        assertEquals("Test Customer", response.getCustomerName());
    }

    @Test
    void getById_ThrowsEntityNotFoundException_WhenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getById(orderId));
    }

    @Test
    void delete_Success() {
        when(orderRepository.existsById(orderId)).thenReturn(true);

        assertDoesNotThrow(() -> orderService.delete(orderId));
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void delete_ThrowsEntityNotFoundException_WhenOrderNotFound() {
        when(orderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.delete(orderId));
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void search_Success() {
        SearchOrderCriteria criteria = new SearchOrderCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        
        OrderSearchProjection projection = new OrderSearchProjection() {
            @Override
            public UUID getId() { return orderId; }
            @Override
            public UUID getCustomerId() { return customerId; }
            @Override
            public String getCustomerName() { return "Test Customer"; }
            @Override
            public String getOrderName() { return "ORD-123456"; }
            @Override
            public String getOrderOrigin() { return "WEB"; }
            @Override
            public String getDeliveryService() { return "NONE"; }
            @Override
            public BigDecimal getPrice() { return BigDecimal.valueOf(100); }
            @Override
            public String getPaymentState() { return "PAID"; }
            @Override
            public String getProcessState() { return "COMPLETED"; }
            @Override
            public UUID getShopId() { return shopId; }
            @Override
            public String getPaymentMethod() { return "CASH"; }
            @Override
            public Instant getOrderDate() { return Instant.now(); }
            @Override
            public Instant getCreatedAt() { return Instant.now(); }
            @Override
            public Instant getUpdatedAt() { return Instant.now(); }
            @Override
            public String getCreatedBy() { return "SYSTEM"; }
            @Override
            public String getUpdatedBy() { return "SYSTEM"; }
            @Override
            public String getDescription() { return "Test Description"; }
        };

        Page<OrderSearchProjection> page = new PageImpl<>(List.of(projection), pageable, 1);

        when(orderRepository.searchOrders(
                criteria.getCustomerId(),
                criteria.getCustomerName(),
                criteria.getOrderOrigin(),
                criteria.getDeliveryService(),
                criteria.getOrderDate(),
                criteria.getShopId(),
                pageable)).thenReturn(page);
                
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());

        PageResponse<OrderResponse> response = orderService.search(criteria, pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(orderId, response.getContent().get(0).getId());
        assertEquals("Test Customer", response.getContent().get(0).getCustomerName());
    }

    @Test
    void create_PreservesRequestedPrice_WhenItemsPresent() {
        request.setPrice(BigDecimal.valueOf(50000));
        com.vanesline.sales.dto.orderitem.OrderItemRequest itemReq = new com.vanesline.sales.dto.orderitem.OrderItemRequest();
        itemReq.setProductId(UUID.randomUUID());
        itemReq.setQuantity(1);
        itemReq.setUnitPrice(BigDecimal.valueOf(20000));
        request.setOrderItems(List.of(itemReq));

        order.setPrice(BigDecimal.valueOf(50000));

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer.builder().fullName("Test Customer").build()));

        OrderResponse response = orderService.create(request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50000), response.getPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void create_CalculatesPrice_WhenPriceIsNull() {
        request.setPrice(null);
        com.vanesline.sales.dto.orderitem.OrderItemRequest itemReq = new com.vanesline.sales.dto.orderitem.OrderItemRequest();
        itemReq.setProductId(UUID.randomUUID());
        itemReq.setQuantity(2);
        itemReq.setUnitPrice(BigDecimal.valueOf(10000));
        request.setOrderItems(List.of(itemReq));

        order.setPrice(BigDecimal.valueOf(20000));

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer.builder().fullName("Test Customer").build()));

        OrderResponse response = orderService.create(request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(20000), response.getPrice());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void exportInvoice_Success() {
        UUID productId = UUID.randomUUID();
        order.setPaymentMethod("CASH");
        order.setDeliveryService("INTERNAL");
        order.setDescription("Invoice note");

        OrderItem orderItem = OrderItem.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(150000))
                .totalPrice(BigDecimal.valueOf(300000))
                .build();

        Shop shop = Shop.builder()
                .shopName("Shop ABC")
                .address("123 Street")
                .build();

        Customer customer = Customer.builder()
                .fullName("Customer A")
                .phoneNumber("0123")
                .address("456 Street")
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("T-Shirt")
                .code("SKU-001")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        InvoiceExportResponse response = orderService.exportInvoice(orderId);

        assertNotNull(response);
        assertEquals("INV-ORD-123456", response.getInvoiceNumber());
        assertEquals("ORD-123456", response.getOrderName());
        assertEquals(BigDecimal.valueOf(300000), response.getSubtotal());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());
        assertEquals(BigDecimal.ZERO, response.getTaxAmount());
        assertEquals(1, response.getOrderItems().size());
        assertEquals("T-Shirt", response.getOrderItems().get(0).getProductName());
        assertEquals("SKU-001", response.getOrderItems().get(0).getProductSku());
        assertEquals("Shop ABC", response.getShop().getName());
        assertEquals("Customer A", response.getCustomer().getName());
    }
}
