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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/category")

public class CategoryController {
    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request){
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update (
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request)
    {
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<CategoryResponse>> search(
            @RequestBody(required = false) SearchCategoryRequest request) {
        if (request == null) {
            request = new SearchCategoryRequest();
        }

        SearchCategoryCriteria criteria = SearchCategoryCriteria .builder()
                .categoryName(request.getCategoryName())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<CategoryResponse> response = categoryService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
