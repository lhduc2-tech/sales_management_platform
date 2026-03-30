package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.inventory.InventoryResponse;
import com.vanesline.sales.dto.inventory.SearchInventoryRequest;
import com.vanesline.sales.dto.inventory.SearchInventoryItemRequest;
import com.vanesline.sales.dto.inventory.AdjustInventoryRequest;
import com.vanesline.sales.dto.inventory.InventoryItemDto;
import com.vanesline.sales.service.contract.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<InventoryResponse> getByProductAndShop(
            @PathVariable UUID productId,
            @PathVariable UUID shopId) {

        return ResponseEntity.ok(
                inventoryService.getByProductAndShop(productId, shopId)
        );
    }

    @PostMapping("/shop")
    @PreAuthorize("hasAuthority('inventory:read')")
    public ResponseEntity<PageResponse<InventoryItemDto>> getInventoryForShop(
            @RequestBody SearchInventoryItemRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return ResponseEntity.ok(
                inventoryService.getInventoryForShop(username, request)
        );
    }

    @PutMapping("/{productId}/adjust")
    public ResponseEntity<InventoryResponse> adjustStock(
            @PathVariable UUID productId,
            @Valid @RequestBody AdjustInventoryRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return ResponseEntity.ok(
                inventoryService.adjustStock(username, productId, request)
        );
    }
}