package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.orderitem.OrderItemRequest;
import com.vanesline.sales.dto.orderitem.OrderItemResponse;
import com.vanesline.sales.dto.orderitem.OrderItemSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {

    OrderItemResponse create(OrderItemRequest request);

    OrderItemResponse update(UUID id, OrderItemRequest request);

    OrderItemResponse getById(UUID id);

    List<OrderItemResponse> getByOrderId(UUID orderId);

    PageResponse<OrderItemResponse> search(OrderItemSearchCriteria criteria, Pageable pageable);

    void delete(UUID id);

    void deleteByOrderId(UUID orderId);
}
