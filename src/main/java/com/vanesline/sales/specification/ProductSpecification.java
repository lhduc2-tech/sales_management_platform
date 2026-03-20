package com.vanesline.sales.specification;

import com.vanesline.sales.dto.product.ProductSearchCriteria;
import com.vanesline.sales.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> withCriteria(ProductSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getCode())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")),
                        "%" + criteria.getCode().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + criteria.getName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getDescription())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + criteria.getDescription().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getSearchQuery())) {
                String queryValue = "%" + criteria.getSearchQuery().toLowerCase() + "%";
                Predicate codeMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), queryValue);
                Predicate nameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), queryValue);
                predicates.add(criteriaBuilder.or(codeMatch, nameMatch));
            }

            if (criteria.getShopId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shopId"), criteria.getShopId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
