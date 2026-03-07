package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.customer.CustomerRequest;
import com.vanesline.sales.dto.customer.CustomerResponse;
import com.vanesline.sales.dto.customer.SearchCustomerCriteria;
import com.vanesline.sales.dto.common.PageResponse;
import com.vanesline.sales.entity.Customer;
import com.vanesline.sales.exception.EntityNotFoundException;
import com.vanesline.sales.repository.CustomerRepository;
import com.vanesline.sales.service.contract.CustomerService;
import com.vanesline.sales.specification.CustomerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse create(CustomerRequest request) {
        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .shopId(request.getShopId())
                .build();

        customer.setCreatedBy(request.getCreatedBy());
        customer.setUpdatedBy(request.getUpdatedBy());
        customer = customerRepository.save(customer);

        return toResponse(customer);
    }

    @Override
    public CustomerResponse update(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setShopId(request.getShopId());
        customer.setUpdatedBy(request.getUpdatedBy());
        customer = customerRepository.save(customer);

        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<CustomerResponse> search(SearchCustomerCriteria criteria, Pageable pageable) {
        Page<Customer> page = customerRepository.findAll(
                CustomerSpecification.withCriteria(criteria),
                pageable);

        Page<CustomerResponse> responsePage = page.map(this::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public void delete(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .shopId(customer.getShopId())
                .createdAt(customer.getCreatedAt())
                .createdBy(customer.getCreatedBy())
                .updatedAt(customer.getUpdatedAt())
                .updatedBy(customer.getUpdatedBy())
                .build();
    }
}
