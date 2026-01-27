package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.shop.ShopRequest;
import com.vanesline.sales.dto.shop.ShopResponse;
import com.vanesline.sales.entity.Shop;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.ShopRepository;
import com.vanesline.sales.service.contract.ShopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    public ShopServiceImpl(ShopRepository shopRepository) {this.shopRepository = shopRepository;}


    @Override
    public ShopResponse create(ShopRequest request) {
        Shop shop = Shop.builder()
                .ownerUserId(request.getOwnerUserId())
                .shopName(request.getShopName())
                .status(request.getStatus())
                .build();
        shop.setCreatedBy(request.getCreatedBy());
        shop.setUpdatedBy(request.getUpdatedBy());
        shop = shopRepository.save(shop);
        return toResponse(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getById(UUID id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shop not found with id: " + id));
        return toResponse(shop);
    }

    @Override
    public void delete(UUID id) {
        if (!shopRepository.existsById(id)) {
            throw new EntityNotFoundException("Shop not found with id: " + id);
        }
        shopRepository.deleteById(id);
    }

    private ShopResponse toResponse(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .ownerShopId(shop.getOwnerUserId())
                .shopName(shop.getShopName())
                .status(shop.getStatus())
                .address(shop.getAddress())
                .aim(shop.getAim())
                .createdAt(shop.getCreatedAt())
                .createdBy(shop.getCreatedBy())
                .updatedAt(shop.getUpdatedAt())
                .updatedBy(shop.getUpdatedBy())
                .build();
    }
}
