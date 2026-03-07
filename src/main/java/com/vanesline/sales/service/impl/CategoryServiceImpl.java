package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.category.CategoryRequest;
import com.vanesline.sales.dto.category.CategoryResponse;
import com.vanesline.sales.dto.category.SearchCategoryCriteria;
import com.vanesline.sales.dto.common.PageResponse;

import com.vanesline.sales.entity.ProductCategory;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.CategoryRepository;
import com.vanesline.sales.service.contract.CategoryService;
import com.vanesline.sales.specification.CategorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl (CategoryRepository categoryRepository){

        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        ProductCategory category = ProductCategory.builder()
                .categoryName(request.getCategoryName())
                .categoryDescription(request.getCategoryDescription())
                .shopId(request.getShopId())
                .build();

        category.setCreatedBy(request.getCreatedBy());
        category.setUpdatedBy(request.getUpdatedBy());
        category = categoryRepository.save(category);

        return toResponse(category);
    }

    @Override

    public CategoryResponse update(UUID id, CategoryRequest request) {

        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product Category not found"));

        category.setCategoryName(request.getCategoryName());
        category.setCategoryDescription(request.getCategoryDescription());
        category.setUpdatedBy(request.getUpdatedBy());
        category.setShopId(request.getShopId());
        category = categoryRepository.save(category);

        return toResponse(category);
    }

    @Override
    @Transactional (readOnly = true)
    public CategoryResponse getById(UUID id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category is not found"));

        return toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<CategoryResponse> search(SearchCategoryCriteria criteria, Pageable pageable) {
        Page <ProductCategory> page = categoryRepository.findAll(
                CategorySpecification.withCriteria(criteria),
                pageable);

        Page<CategoryResponse> responsePage = page.map(this::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Order not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    private CategoryResponse toResponse(ProductCategory category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .categoryDescription(category.getCategoryDescription())
                .shopId(category.getShopId())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
                .build();
    }
}
