package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.staff.StaffRequest;
import com.vanesline.sales.dto.staff.StaffResponse;
import com.vanesline.sales.dto.staff.StaffSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StaffService {

    StaffResponse create(StaffRequest request);

    StaffResponse update(UUID id, StaffRequest request);

    StaffResponse getById(UUID id);

    PageResponse<StaffResponse> search(StaffSearchCriteria criteria, Pageable pageable);

    void delete(UUID id);
}
