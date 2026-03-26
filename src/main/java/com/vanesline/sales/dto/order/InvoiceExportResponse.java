package com.vanesline.sales.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceExportResponse {

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("order_name")
    private String orderName;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    private ShopInfo shop;

    private CustomerInfo customer;

    @JsonProperty("order_items")
    private List<OrderItemInfo> orderItems;

    private BigDecimal subtotal;

    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;

    private BigDecimal price;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_state")
    private String paymentState;

    @JsonProperty("delivery_service")
    private String deliveryService;

    private String description;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopInfo {
        private String name;
        private String address;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String name;
        private String phone;
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfo {
        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("product_sku")
        private String productSku;

        private Integer quantity;

        @JsonProperty("unit_price")
        private BigDecimal unitPrice;

        @JsonProperty("total_price")
        private BigDecimal totalPrice;
    }
}

