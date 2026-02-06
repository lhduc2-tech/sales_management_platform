package com.vanesline.sales.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.vanesline.sales.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Override
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
        super.onCreate();
    }
}
