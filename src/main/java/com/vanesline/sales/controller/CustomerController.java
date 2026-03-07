package com.vanesline.sales.controller;

import com.vanesline.sales.dto.customer.CustomerRequest;
import com.vanesline.sales.dto.customer.CustomerResponse;
import com.vanesline.sales.dto.customer.SearchCustomerCriteria;
import com.vanesline.sales.dto.customer.SearchCustomerRequest;
import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.service.contract.CustomerService;
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
@RequestMapping("${application.path.base.private}/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('customer:write')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:write')")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
        CustomerResponse response = customerService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<List<CustomerResponse>> getAllCustomer() {
        List<CustomerResponse> response = customerService.getAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<PageResponse<CustomerResponse>> search(
            @RequestBody(required = false) SearchCustomerRequest request) {
        if (request == null) {
            request = new SearchCustomerRequest();
        }

        SearchCustomerCriteria criteria = SearchCustomerCriteria.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();

        Sort sort = request.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        PageResponse<CustomerResponse> response = customerService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
