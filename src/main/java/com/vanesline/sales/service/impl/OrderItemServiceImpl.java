package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.orderitem.OrderItemRequest;
import com.vanesline.sales.dto.orderitem.OrderItemResponse;
import com.vanesline.sales.dto.orderitem.OrderItemSearchCriteria;
import com.vanesline.sales.entity.Order;
import com.vanesline.sales.entity.OrderItem;
import com.vanesline.sales.repository.OrderItemRepository;
import com.vanesline.sales.repository.OrderRepository;
import com.vanesline.sales.service.contract.OrderItemService;
import com.vanesline.sales.specification.OrderItemSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderItemResponse create(OrderItemRequest request) {
        // Auto-calculate totalPrice = unitPrice * quantity
        BigDecimal totalPrice = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        OrderItem orderItem = OrderItem.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .orderId(request.getOrderId())
                .totalPrice(totalPrice)
                .unitPrice(request.getUnitPrice())
                .build();

        orderItem.setCreatedBy(request.getCreatedBy());
        orderItem.setUpdatedBy(request.getUpdatedBy());

        orderItem = orderItemRepository.save(orderItem);

        // Update Order's total price
        updateOrderTotalPrice(request.getOrderId());

        return toResponse(orderItem);
    }

    @Override
    public OrderItemResponse update(UUID id, OrderItemRequest request) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: " + id));

        UUID oldOrderId = orderItem.getOrderId();

        // Auto-calculate totalPrice = unitPrice * quantity
        BigDecimal totalPrice = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        orderItem.setProductId(request.getProductId());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setOrderId(request.getOrderId());
        orderItem.setTotalPrice(totalPrice);
        orderItem.setUnitPrice(request.getUnitPrice());
        orderItem.setUpdatedBy(request.getUpdatedBy());

        orderItem = orderItemRepository.save(orderItem);

        // Update Order's total price (update old order if orderId changed)
        updateOrderTotalPrice(request.getOrderId());
        if (!oldOrderId.equals(request.getOrderId())) {
            updateOrderTotalPrice(oldOrderId);
        }

        return toResponse(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponse getById(UUID id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: " + id));
        return toResponse(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getByOrderId(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderItemResponse> search(OrderItemSearchCriteria criteria, Pageable pageable) {
        Page<OrderItem> page = orderItemRepository.findAll(
                OrderItemSpecification.withCriteria(criteria),
                pageable);

        Page<OrderItemResponse> responsePage = page.map(this::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: " + id));

        UUID orderId = orderItem.getOrderId();
        orderItemRepository.deleteById(id);

        updateOrderTotalPrice(orderId);
    }

    @Override
    public void deleteByOrderId(UUID orderId) {
        orderItemRepository.deleteByOrderId(orderId);
        //xóa order item thì cập nhật lại total price của order
        updateOrderTotalPrice(orderId);
    }

    //cập nhật lại total price khi thêm orderitem vào order
    private void updateOrderTotalPrice(UUID orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        order.setPrice(total);
        orderRepository.save(order);
    }

    private OrderItemResponse toResponse(OrderItem orderItem) {
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
