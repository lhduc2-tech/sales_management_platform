package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.category.CategoryRequest;
import com.vanesline.sales.dto.category.CategoryResponse;
import com.vanesline.sales.dto.category.SearchCategoryCriteria;
import com.vanesline.sales.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    CategoryResponse create (CategoryRequest request);

    CategoryResponse update (UUID id, CategoryRequest request);

    CategoryResponse getById (UUID id);

    PageResponse<CategoryResponse> search (SearchCategoryCriteria criteria, Pageable pageable);

    void delete (UUID id);
}
