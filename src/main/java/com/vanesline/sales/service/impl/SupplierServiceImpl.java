package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.dto.supplier.SupplierRequest;
import com.vanesline.sales.dto.supplier.SupplierResponse;
import com.vanesline.sales.dto.supplier.SupplierSearchCriteria;
import com.vanesline.sales.entity.Supplier;
import com.vanesline.sales.exception.DataConflictException;
import com.vanesline.sales.repository.SupplierRepository;
import com.vanesline.sales.service.contract.SupplierService;
import com.vanesline.sales.specification.SupplierSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierResponse create(SupplierRequest request) {
        
        if (StringUtils.hasText(request.getCode()) && supplierRepository.existsByCode(request.getCode())) {
            throw new DataConflictException("Supplier code already exists: " + request.getCode());
        }

        
        if (StringUtils.hasText(request.getEmail()) && supplierRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Email already exists: " + request.getEmail());
        }

        
        if (StringUtils.hasText(request.getPhoneNumber()) && supplierRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DataConflictException("Phone number already exists: " + request.getPhoneNumber());
        }

        
        if (StringUtils.hasText(request.getTaxCode()) && supplierRepository.existsByTaxCode(request.getTaxCode())) {
            throw new DataConflictException("Tax code already exists: " + request.getTaxCode());
        }

        Supplier supplier = Supplier.builder()
                .supplierName(request.getSupplierName())
                .code(request.getCode())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .website(request.getWebsite())
                .taxCode(request.getTaxCode())
                .build();

        supplier.setCreatedBy(request.getCreatedBy());
        supplier.setUpdatedBy(request.getUpdatedBy());

        supplier = supplierRepository.save(supplier);
        return toResponse(supplier);
    }

    @Override
    public SupplierResponse update(UUID id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));

        
        if (StringUtils.hasText(request.getCode()) && !request.getCode().equals(supplier.getCode())) {
            if (supplierRepository.existsByCode(request.getCode())) {
                throw new DataConflictException("Supplier code already exists: " + request.getCode());
            }
        }

        
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(supplier.getEmail())) {
            if (supplierRepository.existsByEmail(request.getEmail())) {
                throw new DataConflictException("Email already exists: " + request.getEmail());
            }
        }

        
        if (StringUtils.hasText(request.getPhoneNumber()) && !request.getPhoneNumber().equals(supplier.getPhoneNumber())) {
            if (supplierRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DataConflictException("Phone number already exists: " + request.getPhoneNumber());
            }
        }

        if (StringUtils.hasText(request.getTaxCode()) && !request.getTaxCode().equals(supplier.getTaxCode())) {
            if (supplierRepository.existsByTaxCode(request.getTaxCode())) {
                throw new DataConflictException("Tax code already exists: " + request.getTaxCode());
            }
        }

        supplier.setSupplierName(request.getSupplierName());
        supplier.setCode(request.getCode());
        supplier.setPhoneNumber(request.getPhoneNumber());
        supplier.setEmail(request.getEmail());
        supplier.setWebsite(request.getWebsite());
        supplier.setTaxCode(request.getTaxCode());
        supplier.setUpdatedBy(request.getUpdatedBy());

        supplier = supplierRepository.save(supplier);
        return toResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
        return toResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SupplierResponse> search(SupplierSearchCriteria criteria, Pageable pageable) {
        Page<Supplier> page = supplierRepository.findAll(
                SupplierSpecification.withCriteria(criteria),
                pageable);

        Page<SupplierResponse> responsePage = page.map(this::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw new EntityNotFoundException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private SupplierResponse toResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .supplierName(supplier.getSupplierName())
                .code(supplier.getCode())
                .phoneNumber(supplier.getPhoneNumber())
                .email(supplier.getEmail())
                .website(supplier.getWebsite())
                .taxCode(supplier.getTaxCode())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .createdBy(supplier.getCreatedBy())
                .updatedBy(supplier.getUpdatedBy())
                .build();
    }
}
