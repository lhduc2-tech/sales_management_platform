package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.product.ProductRequest;
import com.vanesline.sales.dto.product.ProductResponse;
import com.vanesline.sales.dto.product.ProductSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(UUID id, ProductRequest request);

    ProductResponse getById(UUID id);

    PageResponse<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable);

    void delete(UUID id);

    //void getCategoryName(UUID categoryId);
}
