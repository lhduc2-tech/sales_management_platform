package com.vanesline.sales.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Interface projection for native query that joins orders with customers.
 */
public interface OrderSearchProjection {

    UUID getId();

    Instant getOrderDate();

    UUID getCustomerId();

    String getCustomerName();

    String getOrderOrigin();

    BigDecimal getPrice();

    String getPaymentState();

    String getProcessState();

    String getDeliveryService();

    UUID getShopId();

    String getOrderName();

    String getPaymentMethod();

    Instant getCreatedAt();

    String getCreatedBy();

    Instant getUpdatedAt();

    String getUpdatedBy();
}

