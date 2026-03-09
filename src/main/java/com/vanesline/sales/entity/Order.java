package com.vanesline.sales.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.vanesline.sales.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column (name = "order_date")
    private OffsetDateTime orderDate;

    @Column (name = "customer_id")
    private UUID customerId;

    @Column (name = "order_origin")
    private String orderOrigin;

    @Column (name = "price")
    private BigDecimal price;

    @Column (name = "payment_state")
    private String payment_state;

    @Column (name = "process_state")
    private String process_state;

    @Column (name = "delivery_service")
    private String deliveryService;

    @Column (name = "shop_id")
    private UUID shopId;

    @Column (name = "order_name")
    private String orderName;

    @Override
    protected void onCreate() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
        super.onCreate();
    }
}
