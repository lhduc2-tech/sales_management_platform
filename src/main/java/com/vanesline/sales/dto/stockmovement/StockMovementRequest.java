// package com.vanesline.sales.dto.stockmovement;

// import jakarta.validation.constraints.Min;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.util.UUID;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class StockMovementRequest {

//     @NotNull(message = "Product ID is required")
//     private UUID productId;

//     @NotNull(message = "Shop ID is required")
//     private UUID shopId;

//     @NotBlank(message = "Movement type is required")
//     private String movementType;

//     @NotNull(message = "Quantity is required")
//     @Min(value = 1, message = "Quantity must be at least 1")
//     private Integer quantity;

//     @Size(max = 255)
//     private String note;
    
//     private String createdBy;
//     private String updatedBy;
// }
