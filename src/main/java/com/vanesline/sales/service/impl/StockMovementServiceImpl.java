package com.vanesline.sales.service.impl;


import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.stockmovement.SearchStockMovementRequest;
import com.vanesline.sales.dto.stockmovement.StockMovementResponse;
import com.vanesline.sales.entity.StockMovement;
import com.vanesline.sales.entity.Product;
import com.vanesline.sales.repository.ProductRepository;
import com.vanesline.sales.repository.StockMovementRepository;
import com.vanesline.sales.service.contract.StockMovementService;
import lombok.RequiredArgsConstructor;
import com.vanesline.sales.specification.StockMovementSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    public PageResponse<StockMovementResponse> search(SearchStockMovementRequest request, Pageable pageable) {
        Specification<StockMovement> spec = StockMovementSpecification.withCriteria(request);
        Page<StockMovement> page = stockMovementRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    public void logImport(UUID productId, UUID shopId, Integer quantity, String note) {

        StockMovement sm = StockMovement.builder()
                .productId(productId)
                .shopId(shopId)
                .movementType("IMPORT")
                .quantity(quantity)
                .note(note)
                .build();

        stockMovementRepository.save(sm);
    }

    private StockMovementResponse toResponse(StockMovement sm) {

    String productName = productRepository.findById(sm.getProductId())
            .map(Product::getName)
            .orElse("Unknown");

    StockMovementResponse res = new StockMovementResponse();
    res.setId(sm.getId());
    res.setProductId(sm.getProductId());
    res.setProductName(productName); // ✅ OK
    res.setShopId(sm.getShopId());
    res.setMovementType(sm.getMovementType());
    res.setQuantity(sm.getQuantity());
    res.setNote(sm.getNote());
    res.setCreatedAt(sm.getCreatedAt());
    res.setCreatedBy(sm.getCreatedBy());

    return res;
}
}