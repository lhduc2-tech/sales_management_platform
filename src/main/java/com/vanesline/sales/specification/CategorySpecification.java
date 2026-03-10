package com.vanesline.sales.specification;

import com.vanesline.sales.dto.category.SearchCategoryCriteria;
import com.vanesline.sales.entity.ProductCategory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {

    public static Specification<ProductCategory> withCriteria(SearchCategoryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getCategoryName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("categoryName")),
                        "%" + criteria.getCategoryName().toLowerCase() + "%"));
            }

            if (criteria.getShopId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shopId"), criteria.getShopId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
