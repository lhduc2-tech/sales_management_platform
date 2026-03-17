package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.inventory.InventoryResponse;
import com.vanesline.sales.dto.inventory.SearchInventoryRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InventoryService {

    InventoryResponse getByProductAndShop(UUID productId, UUID shopId);

    PageResponse<InventoryResponse> search(SearchInventoryRequest request, Pageable pageable);

    void increaseStock(UUID productId, UUID shopId, Integer quantity);

    void decreaseStock(UUID productId, UUID shopId, Integer quantity);
}