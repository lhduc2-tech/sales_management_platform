package com.vanesline.sales.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProductRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 255)
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private UUID shopId;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;
}
