package com.vanesline.sales.repository;

import com.vanesline.sales.dto.stockmovement.StockMovementResponse;
import com.vanesline.sales.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.List;
@Repository
public interface StockMovementRepository
        extends JpaRepository<StockMovement, UUID>,
        JpaSpecificationExecutor<StockMovement> {
List<StockMovement> findByProductId(UUID productId);
List<StockMovement> findByShopId(UUID shopId);
@Query("""
SELECT new com.vanesline.sales.dto.stockmovement.StockMovementResponse(
    sm.id,
    sm.productId,
    p.name,
    sm.shopId,
    sm.movementType,
    sm.quantity,
    sm.note,
    sm.createdAt,
    sm.createdBy,
    sm.updatedAt,
    sm.updatedBy
)
FROM StockMovement sm
JOIN Product p ON sm.productId = p.id
""")
Page<StockMovementResponse> findAllWithProduct(Pageable pageable);
}
