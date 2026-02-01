package com.vanesline.sales.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.vanesline.sales.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "staffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff extends BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "shop_id")
    private UUID shopId;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Override
    protected void onCreate() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
        super.onCreate();
    }
}
