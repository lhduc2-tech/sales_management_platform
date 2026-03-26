package com.vanesline.sales.specification;

import com.vanesline.sales.dto.stockmovement.SearchStockMovementRequest;
import com.vanesline.sales.entity.StockMovement;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StockMovementSpecification {

    public static Specification<StockMovement> withCriteria(SearchStockMovementRequest criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getShopId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shopId"), criteria.getShopId()));
            }

            if (criteria.getProductId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("productId"), criteria.getProductId()));
            }

            if (StringUtils.hasText(criteria.getMovementType())) {
                predicates.add(criteriaBuilder.equal(root.get("movementType"), criteria.getMovementType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
