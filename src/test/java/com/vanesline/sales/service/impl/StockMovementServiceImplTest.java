package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.stockmovement.SearchStockMovementRequest;
import com.vanesline.sales.dto.stockmovement.StockMovementResponse;
import com.vanesline.sales.entity.StockMovement;
import com.vanesline.sales.repository.ProductRepository;
import com.vanesline.sales.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockMovementServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void search_ShouldUseSpecification() {
        // Arrange
        UUID shopId = UUID.randomUUID();
        SearchStockMovementRequest request = new SearchStockMovementRequest();
        request.setShopId(shopId);
        request.setPage(0);
        request.setSize(10);

        Pageable pageable = PageRequest.of(0, 10);
        StockMovement sm = StockMovement.builder()
                .id(UUID.randomUUID())
                .shopId(shopId)
                .productId(UUID.randomUUID())
                .movementType("IMPORT")
                .quantity(10)
                .build();
        
        Page<StockMovement> page = new PageImpl<>(Collections.singletonList(sm));
        
        when(stockMovementRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(productRepository.findById(any())).thenReturn(java.util.Optional.empty());

        // Act
        PageResponse<StockMovementResponse> response = stockMovementService.search(request, pageable);

        // Assert
        assertEquals(1, response.getContent().size());
        assertEquals(shopId, response.getContent().get(0).getShopId());
        verify(stockMovementRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
