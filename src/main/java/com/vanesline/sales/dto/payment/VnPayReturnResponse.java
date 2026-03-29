package com.vanesline.sales.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VnPayReturnResponse {

    private boolean success;

    private String message;

    @JsonProperty("order_id")
    private UUID orderId;

    @JsonProperty("vnp_response_code")
    private String vnpResponseCode;

    @JsonProperty("vnp_transaction_no")
    private String vnpTransactionNo;

    @JsonProperty("vnp_amount")
    private BigDecimal vnpAmount;
}
