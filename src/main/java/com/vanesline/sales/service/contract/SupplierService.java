package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.supplier.SupplierRequest;
import com.vanesline.sales.dto.supplier.SupplierResponse;
import com.vanesline.sales.dto.supplier.SupplierSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SupplierService {

    SupplierResponse create(SupplierRequest request);

    SupplierResponse update(UUID id, SupplierRequest request);

    SupplierResponse getById(UUID id);

    PageResponse<SupplierResponse> search(SupplierSearchCriteria criteria, Pageable pageable);

    void delete(UUID id);
}
