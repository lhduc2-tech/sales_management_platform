package com.vanesline.sales.dto.product;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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

    @Size(max = 255)
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1000.0", inclusive = true, message = "Price must be greater than or equal to 0")
    @DecimalMax(value = "999999999.99", inclusive = true, message = "Price must be less than or equal to 999,999,999.99")
    @Digits(integer = 9, fraction = 2, message = "Price must have at most 9 integer digits and 2 decimal places")
    private BigDecimal price;

    private UUID shopId;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private UUID categoryId;
}
