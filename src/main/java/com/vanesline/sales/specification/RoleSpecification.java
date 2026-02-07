package com.vanesline.sales.specification;

import com.vanesline.sales.dto.role.RoleSearchCriteria;
import com.vanesline.sales.entity.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class RoleSpecification {

    public static Specification<Role> withCriteria(RoleSearchCriteria criteria) {
        return Specification.allOf(
                hasCode(criteria.getCode()),
                hasName(criteria.getName()),
                hasStatus(criteria.getStatus()));
    }

    private static Specification<Role> hasCode(String code) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(code)) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("code")),
                    "%" + code.toLowerCase() + "%");
        };
    }

    private static Specification<Role> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    private static Specification<Role> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(status)) {
                return null;
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
