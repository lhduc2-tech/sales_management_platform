package com.vanesline.sales.specification;

import com.vanesline.sales.dto.customer.SearchCustomerCriteria;
import com.vanesline.sales.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecification {

    public static Specification<Customer> withCriteria(SearchCustomerCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getFullName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("fullName")),
                        "%" + criteria.getFullName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getPhoneNumber())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("phoneNumber")),
                        "%" + criteria.getPhoneNumber().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getEmail())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + criteria.getEmail().toLowerCase() + "%"));
            }

            if (criteria.getShopId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shopId"), criteria.getShopId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
