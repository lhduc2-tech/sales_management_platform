package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.orderitem.OrderItemRequest;
import com.vanesline.sales.dto.orderitem.OrderItemResponse;
import com.vanesline.sales.dto.orderitem.OrderItemSearchCriteria;
import com.vanesline.sales.dto.orderitem.SearchOrderItemRequest;
import com.vanesline.sales.service.contract.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> create(@Valid @RequestBody OrderItemRequest request) {
        OrderItemResponse response = orderItemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody OrderItemRequest request) {
        OrderItemResponse response = orderItemService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getById(@PathVariable UUID id) {
        OrderItemResponse response = orderItemService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemResponse>> getByOrderId(@PathVariable UUID orderId) {
        List<OrderItemResponse> responses = orderItemService.getByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<OrderItemResponse>> search(
            @RequestBody(required = false) SearchOrderItemRequest request) {
        if (request == null) {
            request = new SearchOrderItemRequest();
        }

        OrderItemSearchCriteria criteria = OrderItemSearchCriteria.builder()
                .productId(request.getProductId())
                .orderId(request.getOrderId())
                .minQuantity(request.getMinQuantity())
                .maxQuantity(request.getMaxQuantity())
                .minTotalPrice(request.getMinTotalPrice())
                .maxTotalPrice(request.getMaxTotalPrice())
                .minUnitPrice(request.getMinUnitPrice())
                .maxUnitPrice(request.getMaxUnitPrice())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<OrderItemResponse> response = orderItemService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
