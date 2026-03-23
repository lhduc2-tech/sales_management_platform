package com.vanesline.sales.repository;

import com.vanesline.sales.entity.OrderItem;
import com.vanesline.sales.dto.dashboard.TopProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>, JpaSpecificationExecutor<OrderItem> {

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByProductId(UUID productId);

    boolean existsByOrderId(UUID orderId);

    boolean existsByProductId(UUID productId);

    void deleteByOrderId(UUID orderId);

    @Query("SELECT new com.vanesline.sales.dto.dashboard.TopProductDto(CAST(p.id AS string), p.name, CAST(SUM(oi.quantity) AS int), SUM(oi.totalPrice)) " +
           "FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "JOIN Product p ON oi.productId = p.id " +
           "WHERE o.shopId = :shopId AND o.paymentState = 'PAID' AND o.updatedAt >= :startDate AND o.updatedAt <= :endDate " +
           "GROUP BY p.id, p.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    Page<TopProductDto> findTopSellingProducts(
            @org.springframework.data.repository.query.Param("shopId") UUID shopId,
            @org.springframework.data.repository.query.Param("startDate") java.time.OffsetDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.OffsetDateTime endDate,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT new com.vanesline.sales.dto.dashboard.TopProductDto(CAST(p.id AS string), p.name, CAST(SUM(oi.quantity) AS int), SUM(oi.totalPrice)) " +
           "FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "JOIN Product p ON oi.productId = p.id " +
           "WHERE o.shopId = :shopId AND o.paymentState = 'PAID' " +
           "GROUP BY p.id, p.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    Page<TopProductDto> findTopSellingProductsAllTime(
            @org.springframework.data.repository.query.Param("shopId") UUID shopId,
            org.springframework.data.domain.Pageable pageable);
}
