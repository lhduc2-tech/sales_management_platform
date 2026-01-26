package com.vanesline.sales.specification;

import com.vanesline.sales.dto.staff.StaffSearchCriteria;
import com.vanesline.sales.entity.Staff;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StaffSpecification {

    public static Specification<Staff> withCriteria(StaffSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getFullName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("fullName")),
                        "%" + criteria.getFullName().toLowerCase() + "%"));
            }

            if (criteria.getShopId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shopId"), criteria.getShopId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
