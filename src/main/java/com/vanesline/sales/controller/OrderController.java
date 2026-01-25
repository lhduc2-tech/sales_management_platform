package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.order.OrderRequest;
import com.vanesline.sales.dto.order.OrderResponse;
import com.vanesline.sales.dto.order.SearchOrderCriteria;
import com.vanesline.sales.dto.order.SearchOrderRequest;
import com.vanesline.sales.service.contract.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/order")

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request){
        OrderResponse response = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update (
            @PathVariable UUID id,
            @Valid @RequestBody OrderRequest request)
    {
        OrderResponse response = orderService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<OrderResponse>> search(
            @RequestBody(required = false) SearchOrderRequest request) {
        if (request == null) {
            request = new SearchOrderRequest();
        }

        SearchOrderCriteria criteria = SearchOrderCriteria.builder()
                .customerName(request.getCustomerName())
                .orderOrigin(request.getOrderOrigin())
                .deliveryService(request.getDeliveryService())
                .orderDate(request.getOrderDate())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<OrderResponse> response = orderService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
