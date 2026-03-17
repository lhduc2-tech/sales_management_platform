package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.order.OrderRequest;
import com.vanesline.sales.dto.order.OrderResponse;
import com.vanesline.sales.dto.order.OrderSearchProjection;
import com.vanesline.sales.dto.order.SearchOrderCriteria;
import com.vanesline.sales.dto.orderitem.OrderItemResponse;
import com.vanesline.sales.entity.Order;
import com.vanesline.sales.entity.OrderItem;
import com.vanesline.sales.repository.CustomerRepository;
import com.vanesline.sales.repository.OrderItemRepository;
import com.vanesline.sales.repository.OrderRepository;
import com.vanesline.sales.service.contract.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl (OrderRepository orderRepository, OrderItemRepository orderItemRepository, CustomerRepository customerRepository){
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public OrderResponse create(OrderRequest request) {
        String generatedOrderName = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Order order = Order.builder()
                .orderDate(request.getOrderDate())
                .customerId(request.getCustomerId())
                .orderOrigin(request.getOrderOrigin())
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .paymentState(request.getPaymentState())
                .processState(request.getProcessState())
                .deliveryService(request.getDeliveryService())
                .shopId(request.getShopId())
                .orderName(generatedOrderName)
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .build();

        order.setCreatedBy(request.getCreatedBy());
        order.setUpdatedBy(request.getUpdatedBy());

        final Order savedOrder = orderRepository.save(order);

        if (request.getOrderItems() != null && !request.getOrderItems().isEmpty()) {
            java.util.List<OrderItem> items = request.getOrderItems().stream()
                    .map(itemReq -> {
                        BigDecimal totalPrice = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                        OrderItem item = OrderItem.builder()
                                .productId(itemReq.getProductId())
                                .quantity(itemReq.getQuantity())
                                .orderId(savedOrder.getId())
                                .unitPrice(itemReq.getUnitPrice())
                                .totalPrice(totalPrice)
                                .build();
                        item.setCreatedBy(request.getCreatedBy());
                        item.setUpdatedBy(request.getUpdatedBy());
                        return item;
                    })
                    .collect(Collectors.toList());
            orderItemRepository.saveAll(items);

            // Update order total price ONLY if not provided or zero
            if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal total = items.stream()
                        .map(OrderItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                savedOrder.setPrice(total);
                orderRepository.save(savedOrder);
            }
        }

        return toResponse(savedOrder);
    }

    @Override
    public OrderResponse update(UUID id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        order.setOrderDate(request.getOrderDate());
        order.setCustomerId(request.getCustomerId());
        order.setOrderOrigin(request.getOrderOrigin());
        order.setPrice(request.getPrice());
        order.setPaymentState(request.getPaymentState());
        order.setProcessState(request.getProcessState());
        order.setDeliveryService(request.getDeliveryService());
        order.setShopId(request.getShopId());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setDescription(request.getDescription());
        order.setUpdatedBy(request.getUpdatedBy());

        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    @Transactional (readOnly = true)
    public OrderResponse getById(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return toResponse(order);
    }

    @Override
    @Transactional (readOnly = true)
    public PageResponse<OrderResponse> search(SearchOrderCriteria criteria, Pageable pageable) {
        Page<OrderSearchProjection> page = orderRepository.searchOrders(
                criteria.getCustomerId(),
                criteria.getCustomerName(),
                criteria.getOrderOrigin(),
                criteria.getDeliveryService(),
                criteria.getOrderDate(),
                criteria.getShopId(),
                pageable);

        Page<OrderResponse> responsePage = page.map(this::toResponseFromProjection);
        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> orderItemResponses = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        String customerName = null;
        String phoneNumber = null;
        String email = null;
        String address = null;

        if (order.getCustomerId() != null) {
            com.vanesline.sales.entity.Customer customer = customerRepository.findById(order.getCustomerId()).orElse(null);
            if (customer != null) {
                customerName = customer.getFullName();
                phoneNumber = customer.getPhoneNumber();
                email = customer.getEmail();
                address = customer.getAddress();
            }
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderItems(orderItemResponses)
                .customerId(order.getCustomerId())
                .customerName(customerName)
                .phoneNumber(phoneNumber)
                .email(email)
                .address(address)
                .orderDate(order.getOrderDate())
                .orderOrigin(order.getOrderOrigin())
                .price(order.getPrice())
                .paymentState(order.getPaymentState())
                .processState(order.getProcessState())
                .deliveryService(order.getDeliveryService())
                .shopId(order.getShopId())
                .orderName(order.getOrderName())
                .paymentMethod(order.getPaymentMethod())
                .description(order.getDescription())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .createdBy(order.getCreatedBy())
                .updatedBy(order.getUpdatedBy())
                .build();
    }

    private OrderResponse toResponseFromProjection(OrderSearchProjection p) {
        List<OrderItemResponse> orderItemResponses = orderItemRepository.findByOrderId(p.getId())
                .stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(p.getId())
                .orderItems(orderItemResponses)
                .customerId(p.getCustomerId())
                .customerName(p.getCustomerName())
                .orderDate(toOffsetDateTime(p.getOrderDate()))
                .orderOrigin(p.getOrderOrigin())
                .price(p.getPrice())
                .paymentState(p.getPaymentState())
                .processState(p.getProcessState())
                .deliveryService(p.getDeliveryService())
                .shopId(p.getShopId())
                .orderName(p.getOrderName())
                .paymentMethod(p.getPaymentMethod())
                .description(p.getDescription())
                .createdAt(toOffsetDateTime(p.getCreatedAt()))
                .updatedAt(toOffsetDateTime(p.getUpdatedAt()))
                .createdBy(p.getCreatedBy())
                .updatedBy(p.getUpdatedBy())
                .build();
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .orderId(orderItem.getOrderId())
                .totalPrice(orderItem.getTotalPrice())
                .unitPrice(orderItem.getUnitPrice())
                .createdAt(orderItem.getCreatedAt())
                .updatedAt(orderItem.getUpdatedAt())
                .createdBy(orderItem.getCreatedBy())
                .updatedBy(orderItem.getUpdatedBy())
                .build();
    }
}
