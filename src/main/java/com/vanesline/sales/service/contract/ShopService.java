package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.shop.ShopRequest;
import com.vanesline.sales.dto.shop.ShopResponse;

import java.util.UUID;

public interface ShopService {

    ShopResponse create (ShopRequest request);

    ShopResponse getById(UUID id);

    void delete(UUID id);
}
