package com.vanesline.sales.controller;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.supplier.SupplierRequest;
import com.vanesline.sales.dto.supplier.SupplierResponse;
import com.vanesline.sales.dto.supplier.SupplierSearchCriteria;
import com.vanesline.sales.dto.supplier.SearchSupplierRequest;
import com.vanesline.sales.service.contract.SupplierService;
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
@RequestMapping("${application.path.base.private}/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('supplier:write')")
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:write')")
    public ResponseEntity<SupplierResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:read')")
    public ResponseEntity<SupplierResponse> getById(@PathVariable UUID id) {
        SupplierResponse response = supplierService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('supplier:read')")
    public ResponseEntity<PageResponse<SupplierResponse>> search(
            @RequestBody(required = false) SearchSupplierRequest request) {
        if (request == null) {
            request = new SearchSupplierRequest();
        }

        SupplierSearchCriteria criteria = SupplierSearchCriteria.builder()
                .supplierName(request.getSupplierName())
                .code(request.getCode())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .taxCode(request.getTaxCode())
                .website(request.getWebsite())
                .shopId(request.getShopId())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<SupplierResponse> response = supplierService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
