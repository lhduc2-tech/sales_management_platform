package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.product.ProductRequest;
import com.vanesline.sales.dto.product.ProductResponse;
import com.vanesline.sales.dto.product.ProductSearchCriteria;
import com.vanesline.sales.dto.product.SearchProductRequest;
import com.vanesline.sales.service.contract.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${application.path.base.private}/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:write')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:write')")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        ProductResponse response = productService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<PageResponse<ProductResponse>> search(
            @RequestBody(required = false) SearchProductRequest request) {
        if (request == null) {
            request = new SearchProductRequest();
        }

        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .code(request.getCode())
                .name(request.getName())
                .shopId(request.getShopId())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<ProductResponse> response = productService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
