package com.vanesline.sales.specification;

import com.vanesline.sales.dto.supplier.SupplierSearchCriteria;
import com.vanesline.sales.entity.Supplier;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SupplierSpecification {

    public static Specification<Supplier> withCriteria(SupplierSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getSupplierName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("supplierName")),
                        "%" + criteria.getSupplierName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getCode())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")),
                        "%" + criteria.getCode().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getPhoneNumber())) {
                predicates.add(criteriaBuilder.like(
                        root.get("phoneNumber"),
                        "%" + criteria.getPhoneNumber() + "%"));
            }

            if (StringUtils.hasText(criteria.getEmail())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + criteria.getEmail().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getTaxCode())) {
                predicates.add(criteriaBuilder.like(
                        root.get("taxCode"),
                        "%" + criteria.getTaxCode() + "%"));
            }

            if (StringUtils.hasText(criteria.getWebsite())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("website")),
                        "%" + criteria.getWebsite().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
