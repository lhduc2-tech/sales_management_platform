package com.vanesline.sales.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank (message = "Category Name is required")
    @Size(max = 255)
    private String categoryName;

    private String categoryDescription;

    private UUID shopId;

    private String createdBy;

    private String updatedBy;
}
