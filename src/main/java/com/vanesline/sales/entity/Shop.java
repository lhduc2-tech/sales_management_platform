package com.vanesline.sales.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.vanesline.sales.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop extends BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "status")
    private String status;

    @Column(name = "address")
    private String address;

    @Column(name = "shop_type", length = 30)
    private String shopType;

    @Column(name = "shop_code", length = 50, unique = true)
    private String shopCode;

    @Column(name = "opened_at")
    private OffsetDateTime openedAt;

    @Override
    protected void onCreate() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
        super.onCreate();
    }
}
