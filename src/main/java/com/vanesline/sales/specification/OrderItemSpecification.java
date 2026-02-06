package com.vanesline.sales.specification;

import com.vanesline.sales.dto.orderitem.OrderItemSearchCriteria;
import com.vanesline.sales.entity.OrderItem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderItemSpecification {

    public static Specification<OrderItem> withCriteria(OrderItemSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getProductId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("productId"), criteria.getProductId()));
            }

            if (criteria.getOrderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderId"), criteria.getOrderId()));
            }

            if (criteria.getMinQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), criteria.getMinQuantity()));
            }

            if (criteria.getMaxQuantity() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("quantity"), criteria.getMaxQuantity()));
            }

            if (criteria.getMinTotalPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalPrice"), criteria.getMinTotalPrice()));
            }

            if (criteria.getMaxTotalPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalPrice"), criteria.getMaxTotalPrice()));
            }

            if (criteria.getMinUnitPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("unitPrice"), criteria.getMinUnitPrice()));
            }

            if (criteria.getMaxUnitPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("unitPrice"), criteria.getMaxUnitPrice()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
