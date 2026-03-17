package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.inventory.InventoryResponse;
import com.vanesline.sales.dto.inventory.SearchInventoryRequest;
import com.vanesline.sales.service.contract.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('inventory:read')")
    public ResponseEntity<PageResponse<InventoryResponse>> search(
            @ModelAttribute SearchInventoryRequest request) {

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        return ResponseEntity.ok(
                inventoryService.search(request, pageable)
        );
    }

    @GetMapping("/{productId}/{shopId}")
    @PreAuthorize("hasAuthority('inventory:read')")
    public ResponseEntity<InventoryResponse> getByProductAndShop(
            @PathVariable UUID productId,
            @PathVariable UUID shopId) {

        return ResponseEntity.ok(
                inventoryService.getByProductAndShop(productId, shopId)
        );
    }
}