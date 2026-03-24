package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.inventory.InventoryResponse;
import com.vanesline.sales.dto.inventory.SearchInventoryRequest;
import com.vanesline.sales.dto.inventory.InventoryItemDto;
import com.vanesline.sales.service.contract.InventoryService;
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
    @PreAuthorize("hasAuthority('inventory:read')")
    public ResponseEntity<InventoryResponse> getByProductAndShop(
            @PathVariable UUID productId,
            @PathVariable UUID shopId) {

        return ResponseEntity.ok(
                inventoryService.getByProductAndShop(productId, shopId)
        );
    }

    @GetMapping("/shop")
    @PreAuthorize("hasAuthority('inventory:read')")
    public ResponseEntity<PageResponse<InventoryItemDto>> getInventoryForShop(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                inventoryService.getInventoryForShop(username, pageable)
        );
    }
}