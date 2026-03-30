package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.inventory.InventoryResponse;
import com.vanesline.sales.dto.inventory.SearchInventoryRequest;
import com.vanesline.sales.dto.inventory.SearchInventoryItemRequest;
import com.vanesline.sales.dto.inventory.AdjustInventoryRequest;
import com.vanesline.sales.dto.inventory.InventoryItemDto;
import com.vanesline.sales.entity.Inventory;
import com.vanesline.sales.entity.Product;
import com.vanesline.sales.entity.StockMovement;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.InventoryRepository;
import com.vanesline.sales.repository.ProductRepository;
import com.vanesline.sales.repository.StockMovementRepository;
import com.vanesline.sales.repository.UserRepository;
import com.vanesline.sales.service.contract.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getByProductAndShop(UUID productId, UUID shopId) {
        Inventory inv = inventoryRepository
                .findByProductIdAndShopId(productId, shopId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        return toResponse(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryResponse> search(SearchInventoryRequest request, Pageable pageable) {

        Page<Inventory> page = inventoryRepository.findAll(pageable);

        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryItemDto> getInventoryForShop(String username, SearchInventoryItemRequest request) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        
        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<InventoryItemDto> page = inventoryRepository.getInventoryForShop(
                user.getShopId(), 
                request.getSearchQuery(), 
                pageable);
        
        return PageResponse.from(page);
    }

    @Override
    public void increaseStock(UUID productId, UUID shopId, Integer quantity) {

        Inventory inv = inventoryRepository
                .findByProductIdAndShopId(productId, shopId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .shopId(shopId)
                        .quantity(0)
                        .build());

        inv.setQuantity(inv.getQuantity() + quantity);
        inventoryRepository.save(inv);
    }

    @Override
    public void decreaseStock(UUID productId, UUID shopId, Integer quantity) {

        Inventory inv = inventoryRepository
                .findByProductIdAndShopId(productId, shopId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        if (inv.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        inv.setQuantity(inv.getQuantity() - quantity);
        inventoryRepository.save(inv);
    }

    @Override
    public InventoryResponse adjustStock(String username, UUID productId, AdjustInventoryRequest request) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        
        UUID shopId = user.getShopId();

        Inventory inv = inventoryRepository
                .findByProductIdAndShopId(productId, shopId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .shopId(shopId)
                        .quantity(0)
                        .build());

        int oldQuantity = inv.getQuantity() != null ? inv.getQuantity() : 0;
        int newQuantity = request.getNewQuantity();
        int quantityDiff = newQuantity - oldQuantity;

        if (quantityDiff == 0) {
            return toResponse(inv);
        }

        inv.setQuantity(newQuantity);
        inventoryRepository.save(inv);

        String combinedNote = "";
        if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
            combinedNote += "[" + request.getReason().trim() + "] ";
        }
        if (request.getNote() != null && !request.getNote().trim().isEmpty()) {
            combinedNote += request.getNote().trim();
        }

        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .shopId(shopId)
                .movementType("ADJUST")
                .quantity(quantityDiff)
                .note(combinedNote.trim())
                .build();
        
        stockMovementRepository.save(movement);

        return toResponse(inv);
    }

private InventoryResponse toResponse(Inventory inv) {

    String productName = productRepository.findById(inv.getProductId())
            .map(Product::getName)
            .orElse(null);

    return InventoryResponse.builder()
            .productId(inv.getProductId())
            .productName(productName)
            .shopId(inv.getShopId())
            .quantity(inv.getQuantity())
            .updatedAt(inv.getUpdatedAt())
            .build();
}
}