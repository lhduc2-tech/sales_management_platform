package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.order.OrderRequest;
import com.vanesline.sales.dto.order.OrderResponse;
import com.vanesline.sales.dto.order.SearchOrderCriteria;
import com.vanesline.sales.dto.orderitem.OrderItemResponse;
import com.vanesline.sales.dto.staff.StaffResponse;
import com.vanesline.sales.entity.Order;
import com.vanesline.sales.entity.OrderItem;
import com.vanesline.sales.repository.OrderItemRepository;
import com.vanesline.sales.repository.OrderRepository;
import com.vanesline.sales.service.contract.OrderService;
import com.vanesline.sales.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;



import java.util.UUID;

@Service
@Transactional

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl (OrderRepository orderRepository, OrderItemRepository orderItemRepository){
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderResponse create(OrderRequest request) {

        Order order = Order.builder()
                .orderDate(request.getOrderDate())
                .customerName(request.getCustomerName())
                .orderOrigin(request.getOrderOrigin())
                .price(request.getPrice())
                .payment_state(request.getPaymentState())
                .process_state(request.getProcessState())
                .deliveryService(request.getDeliveryService())
                .shopId(request.getShopId())
                .orderName(request.getOrderName())
                .build();

        order.setCreatedBy(request.getCreatedBy());
        order.setUpdatedBy(request.getUpdatedBy());

        order = orderRepository.save(order);

        return toResponse(order);
    }

    @Override
    public OrderResponse update(UUID id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        order.setOrderDate(request.getOrderDate());
        order.setCustomerName(request.getCustomerName());
        order.setOrderOrigin(request.getOrderOrigin());
        order.setPrice(request.getPrice());
        order.setPayment_state(request.getPaymentState());
        order.setProcess_state(request.getProcessState());
        order.setDeliveryService(request.getDeliveryService());
        order.setShopId(request.getShopId());
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
        Page <Order> page = orderRepository.findAll(
                OrderSpecification.withCriteria(criteria),
                pageable);

        Page<OrderResponse> responsePage = page.map(this::toResponse);
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

        return OrderResponse.builder()
                .id(order.getId())
                .orderItems(orderItemResponses)
                .customerName(order.getCustomerName())
                .orderDate(order.getOrderDate())
                .orderOrigin(order.getOrderOrigin())
                .price(order.getPrice())
                .paymentState(order.getPayment_state())
                .processState(order.getProcess_state())
                .deliveryService(order.getDeliveryService())
                .shopId(order.getShopId())
                .orderName(order.getOrderName())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .createdBy(order.getCreatedBy())
                .updatedBy(order.getUpdatedBy())
                .build();
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
