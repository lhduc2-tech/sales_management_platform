package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.stockmovement.SearchStockMovementRequest;
import com.vanesline.sales.dto.stockmovement.StockMovementResponse;
import com.vanesline.sales.service.contract.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${application.path.base.private}/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('inventory:read')")
    public ResponseEntity<PageResponse<StockMovementResponse>> search(
            @RequestBody(required = false) SearchStockMovementRequest request) {

        if (request == null) {
            request = new SearchStockMovementRequest();
        }

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        return ResponseEntity.ok(
                stockMovementService.search(request, pageable)
        );
    }
}