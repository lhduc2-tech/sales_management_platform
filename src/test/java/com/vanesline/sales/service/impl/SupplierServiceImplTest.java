package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.supplier.SupplierRequest;
import com.vanesline.sales.dto.supplier.SupplierResponse;
import com.vanesline.sales.entity.Supplier;
import com.vanesline.sales.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;
    private SupplierRequest request;
    private UUID supplierId;
    private UUID shopId;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();
        shopId = UUID.randomUUID();

        supplier = Supplier.builder()
                .supplierName("Test Supplier")
                .code("SUP001")
                .phoneNumber("0123456789")
                .email("test@supplier.com")
                .website("http://test.com")
                .taxCode("12345678")
                .shopId(shopId)
                .build();
        supplier.setId(supplierId);

        request = SupplierRequest.builder()
                .supplierName("Test Supplier")
                .code("SUP001")
                .phoneNumber("0123456789")
                .email("test@supplier.com")
                .website("http://test.com")
                .taxCode("12345678")
                .shopId(shopId)
                .build();
    }

    @Test
    void create_Success_ShouldIncludeShopId() {
        when(supplierRepository.existsByCode(request.getCode())).thenReturn(false);
        when(supplierRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(supplierRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(supplierRepository.existsByTaxCode(request.getTaxCode())).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        SupplierResponse response = supplierService.create(request);

        assertNotNull(response);
        assertEquals(shopId, response.getShopId());
        verify(supplierRepository).save(argThat(s -> shopId.equals(s.getShopId())));
    }

    @Test
    void update_Success_ShouldIncludeShopId() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        SupplierResponse response = supplierService.update(supplierId, request);

        assertNotNull(response);
        assertEquals(shopId, response.getShopId());
        verify(supplierRepository).save(argThat(s -> shopId.equals(s.getShopId())));
    }

    @Test
    void getById_Success_ShouldIncludeShopId() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));

        SupplierResponse response = supplierService.getById(supplierId);

        assertNotNull(response);
        assertEquals(shopId, response.getShopId());
    }
}
