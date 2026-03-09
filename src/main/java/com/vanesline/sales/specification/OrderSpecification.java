package com.vanesline.sales.specification;

import com.vanesline.sales.dto.order.SearchOrderCriteria;

import com.vanesline.sales.entity.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class OrderSpecification {

    public static Specification<Order> withCriteria (SearchOrderCriteria criteria){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getCustomerName())){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("customerName")),
                        "%" + criteria.getCustomerName().toLowerCase() + "%"));

            }
            if (StringUtils.hasText(criteria.getOrderOrigin())){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("orderOrigin")),
                        "%" + criteria.getOrderOrigin().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getDeliveryService())){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("deliveryService")),
                        "%" + criteria.getDeliveryService().toLowerCase() + "%"));
            }

            if (criteria.getOrderDate()!= null)
            {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("orderDate"),
                                criteria.getOrderDate()
                        )
                );
            }

            if (criteria.getShopId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shopId"), criteria.getShopId()));
            }

            return criteriaBuilder.and(predicates.toArray (new Predicate[0]));
        };

    }
}
