package com.vanesline.sales.repository;

import com.vanesline.sales.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>, JpaSpecificationExecutor<OrderItem> {

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByProductId(UUID productId);

    boolean existsByOrderId(UUID orderId);

    boolean existsByProductId(UUID productId);

    void deleteByOrderId(UUID orderId);
}
