package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.product.ProductRequest;
import com.vanesline.sales.dto.product.ProductResponse;
import com.vanesline.sales.dto.product.ProductSearchCriteria;
import com.vanesline.sales.entity.Product;
import com.vanesline.sales.exception.DataConflictException;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.ProductRepository;
import com.vanesline.sales.service.contract.ProductService;
import com.vanesline.sales.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        if (StringUtils.hasText(request.getCode()) && productRepository.existsByCode(request.getCode())) {
            throw new DataConflictException("Product code already exists: " + request.getCode());
        }

        Product product = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .price(request.getPrice())
                .shopId(request.getShopId())
                .build();

        product.setCreatedBy(request.getCreatedBy());
        product.setUpdatedBy(request.getUpdatedBy());

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Override
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        if (StringUtils.hasText(request.getCode()) && !request.getCode().equals(product.getCode())) {
            if (productRepository.existsByCode(request.getCode())) {
                throw new DataConflictException("Product code already exists: " + request.getCode());
            }
        }

        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setUpdatedBy(request.getUpdatedBy());
        product.setShopId(request.getShopId());

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable) {
        Page<Product> page = productRepository.findAll(
                ProductSpecification.withCriteria(criteria),
                pageable
        );

        Page<ProductResponse> responsePage = page.map(this::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .shopId(product.getShopId())
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .createdBy(product.getCreatedBy())
                .updatedBy(product.getUpdatedBy())
                .build();
    }
}
