package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.customer.CustomerRequest;
import com.vanesline.sales.dto.customer.CustomerResponse;
import com.vanesline.sales.dto.customer.SearchCustomerCriteria;
import com.vanesline.sales.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(UUID id, CustomerRequest request);

    CustomerResponse getById(UUID id);

    List<CustomerResponse> getAll();

    PageResponse<CustomerResponse> search(SearchCustomerCriteria criteria, Pageable pageable);

    void delete(UUID id);
}
