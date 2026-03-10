package com.vanesline.sales.controller;

import com.vanesline.sales.dto.category.CategoryRequest;
import com.vanesline.sales.dto.category.CategoryResponse;
import com.vanesline.sales.dto.category.SearchCategoryCriteria;
import com.vanesline.sales.dto.category.SearchCategoryRequest;
import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.service.contract.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('category:write')")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('category:write')")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<List<CategoryResponse>> getAllCategory() {
        List<CategoryResponse> response = categoryService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-shop/{shopId}")
    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<List<CategoryResponse>> getAllCategoryByShopId(@PathVariable UUID shopId) {
        List<CategoryResponse> response = categoryService.getAllByShopId(shopId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<PageResponse<CategoryResponse>> search(
            @RequestBody(required = false) SearchCategoryRequest request) {
        if (request == null) {
            request = new SearchCategoryRequest();
        }

        SearchCategoryCriteria criteria = SearchCategoryCriteria.builder()
                .categoryName(request.getCategoryName())
                .shopId(request.getShopId())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<CategoryResponse> response = categoryService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('category:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
