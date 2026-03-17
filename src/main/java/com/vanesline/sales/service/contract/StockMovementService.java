package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.stockmovement.SearchStockMovementRequest;
import com.vanesline.sales.dto.stockmovement.StockMovementResponse;
import org.springframework.data.domain.Pageable;

public interface StockMovementService {

    PageResponse<StockMovementResponse> search(SearchStockMovementRequest request, Pageable pageable);

    void logImport(java.util.UUID productId, java.util.UUID shopId, Integer quantity, String note);
}