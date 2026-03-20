package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.product.ProductRequest;
import com.vanesline.sales.dto.product.ProductResponse;
import com.vanesline.sales.dto.product.ProductSearchCriteria;
import com.vanesline.sales.entity.Product;
import com.vanesline.sales.entity.ProductCategory;
import com.vanesline.sales.exception.DataConflictException;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.CategoryRepository;
import com.vanesline.sales.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest request;
    private UUID productId;
    private UUID shopId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        shopId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        product = Product.builder()
                .code("PRD001")
                .name("Test Product")
                .price(BigDecimal.valueOf(100.0))
                .shopId(shopId)
                .categoryId(categoryId)
                .description("Test Description")
                .imgUrl("http://test.com/img.png")
                .build();
        product.setId(productId);

        request = ProductRequest.builder()
                .code("PRD001")
                .name("Test Product")
                .price(BigDecimal.valueOf(100.0))
                .shopId(shopId)
                .categoryId(categoryId)
                .description("Test Description")
                .imgUrl("http://test.com/img.png")
                .build();
    }

    @Test
    void create_Success() {
        when(productRepository.existsByCode(request.getCode())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(ProductCategory.builder().categoryName("Category 1").build()));

        ProductResponse response = productService.create(request);

        assertNotNull(response);
        assertEquals(product.getId(), response.getId());
        assertEquals(request.getCode(), response.getCode());
        assertEquals(request.getName(), response.getName());
        assertEquals("Category 1", response.getCategoryName());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getImgUrl(), response.getImgUrl());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_ThrowsDataConflictException_WhenCodeExists() {
        when(productRepository.existsByCode(request.getCode())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.update(productId, request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_ThrowsEntityNotFoundException_WhenProductDoesNotExist() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(productId, request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getById_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getById(productId);

        assertNotNull(response);
        assertEquals(productId, response.getId());
    }

    @Test
    void getById_ThrowsEntityNotFoundException_WhenNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getById(productId));
    }

    @Test
    void getAllByShopId_Success() {
        when(productRepository.findByShopIdOrderByUpdatedAtDesc(shopId)).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getAllByShopId(shopId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(shopId, responses.get(0).getShopId());
    }

    @Test
    void delete_Success() {
        when(productRepository.existsById(productId)).thenReturn(true);

        assertDoesNotThrow(() -> productService.delete(productId));
        verify(productRepository).deleteById(productId);
    }

    @Test
    void delete_ThrowsEntityNotFoundException_WhenNotFound() {
        when(productRepository.existsById(productId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> productService.delete(productId));
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void search_Success() {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);

        PageResponse<ProductResponse> response = productService.search(criteria, pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(product.getId(), response.getContent().get(0).getId());
    }

    @Test
    void search_WithSearchQuery_Success() {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        criteria.setSearchQuery("test");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);

        PageResponse<ProductResponse> response = productService.search(criteria, pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(product.getId(), response.getContent().get(0).getId());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }
}
