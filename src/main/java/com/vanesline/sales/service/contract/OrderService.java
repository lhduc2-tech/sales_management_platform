package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.order.OrderRequest;
import com.vanesline.sales.dto.order.OrderResponse;
import com.vanesline.sales.dto.order.SearchOrderCriteria;
import com.vanesline.sales.dto.order.SearchOrderRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    OrderResponse create (OrderRequest request);

    OrderResponse update (UUID id, OrderRequest request);

    OrderResponse getById (UUID id);

    PageResponse <OrderResponse> search (SearchOrderCriteria criteria, Pageable pageable);

    void delete (UUID id);
}
