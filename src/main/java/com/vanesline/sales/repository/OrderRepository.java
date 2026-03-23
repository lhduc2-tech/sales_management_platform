package com.vanesline.sales.repository;

import com.vanesline.sales.dto.order.OrderSearchProjection;
import com.vanesline.sales.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @Query(value = """
            SELECT o.id              AS id,
                   o.order_date      AS orderDate,
                   o.customer_id     AS customerId,
                   c.full_name       AS customerName,
                   o.order_origin    AS orderOrigin,
                   o.price           AS price,
                   o.payment_state   AS paymentState,
                   o.process_state   AS processState,
                   o.delivery_service AS deliveryService,
                   o.shop_id         AS shopId,
                   o.order_name      AS orderName,
                   o.payment_method  AS paymentMethod,
                   o.created_at      AS createdAt,
                   o.created_by      AS createdBy,
                   o.updated_at      AS updatedAt,
                   o.updated_by      AS updatedBy,
                   o.description     AS description
            FROM orders o
            LEFT JOIN customers c ON o.customer_id = c.id
            WHERE (:customerId IS NULL OR o.customer_id = :customerId)
              AND (:customerName IS NULL OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :customerName, '%')))
              AND (:orderOrigin IS NULL OR LOWER(o.order_origin) LIKE LOWER(CONCAT('%', :orderOrigin, '%')))
              AND (:deliveryService IS NULL OR LOWER(o.delivery_service) LIKE LOWER(CONCAT('%', :deliveryService, '%')))
              AND (CAST(:orderDate AS TIMESTAMP) IS NULL OR o.order_date <= :orderDate)
              AND (:shopId IS NULL OR o.shop_id = :shopId)
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM orders o
            LEFT JOIN customers c ON o.customer_id = c.id
            WHERE (:customerId IS NULL OR o.customer_id = :customerId)
              AND (:customerName IS NULL OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :customerName, '%')))
              AND (:orderOrigin IS NULL OR LOWER(o.order_origin) LIKE LOWER(CONCAT('%', :orderOrigin, '%')))
              AND (:deliveryService IS NULL OR LOWER(o.delivery_service) LIKE LOWER(CONCAT('%', :deliveryService, '%')))
              AND (CAST(:orderDate AS TIMESTAMP) IS NULL OR o.order_date <= :orderDate)
              AND (:shopId IS NULL OR o.shop_id = :shopId)
            """,
            nativeQuery = true)
    Page<OrderSearchProjection> searchOrders(
            @Param("customerId") UUID customerId,
            @Param("customerName") String customerName,
            @Param("orderOrigin") String orderOrigin,
            @Param("deliveryService") String deliveryService,
            @Param("orderDate") OffsetDateTime orderDate,
            @Param("shopId") UUID shopId,
            Pageable pageable);

    @Query("SELECT SUM(o.price) FROM Order o WHERE o.shopId = :shopId AND o.paymentState = 'PAID' AND o.updatedAt >= :startDate AND o.updatedAt <= :endDate")
    BigDecimal calculateRevenue(@Param("shopId") UUID shopId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT SUM(o.price) FROM Order o WHERE o.shopId = :shopId AND o.paymentState = 'PAID'")
    BigDecimal calculateTotalRevenue(@Param("shopId") UUID shopId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shopId = :shopId AND o.updatedAt >= :startDate AND o.updatedAt <= :endDate")
    Integer countOrdersInPeriod(@Param("shopId") UUID shopId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shopId = :shopId AND o.processState = :state AND o.updatedAt >= :startDate AND o.updatedAt <= :endDate")
    Integer countOrdersByProcessStateInPeriod(@Param("shopId") UUID shopId, @Param("state") String state, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT AVG(o.price) FROM Order o WHERE o.shopId = :shopId AND o.updatedAt >= :startDate AND o.updatedAt <= :endDate")
    BigDecimal calculateAverageOrderValueInPeriod(@Param("shopId") UUID shopId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);
}
