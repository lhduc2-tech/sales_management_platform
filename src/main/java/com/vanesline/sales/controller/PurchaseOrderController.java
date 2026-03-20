package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.purchaseorder.*;
import com.vanesline.sales.dto.purchaseorderitem.*;
import com.vanesline.sales.service.contract.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasAuthority('purchase_order:write')")
    public ResponseEntity<UUID> create(@Valid @RequestBody PurchaseOrderRequest request) {
        UUID id = purchaseOrderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAuthority('purchase_order:write')")
    public ResponseEntity<Void> addItem(
            @PathVariable UUID id,
            @Valid @RequestBody PurchaseOrderItemRequest request) {

        purchaseOrderService.addItem(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Void> updateItem(
            @PathVariable UUID itemId,
            @RequestBody UpdatePurchaseOrderItemRequest request) {

        purchaseOrderService.updateItem(itemId, request);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {

        purchaseOrderService.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('purchase_order:write')")
    public ResponseEntity<Void> confirm(@PathVariable UUID id) {
        purchaseOrderService.confirm(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('purchase_order:read')")
    public ResponseEntity<PurchaseOrderResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.getById(id));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('purchase_order:read')")
    public ResponseEntity<PageResponse<PurchaseOrderResponse>> search(
            @RequestBody(required = false) SearchPurchaseOrderRequest request) {

        if (request == null) {
            request = new SearchPurchaseOrderRequest();
        }

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        return ResponseEntity.ok(
                purchaseOrderService.search(request, pageable)
        );
    }
}