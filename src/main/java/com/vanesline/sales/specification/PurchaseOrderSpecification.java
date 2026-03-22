package com.vanesline.sales.specification;

import com.vanesline.sales.dto.purchaseorder.SearchPurchaseOrderRequest;
import com.vanesline.sales.entity.PurchaseOrder;
import com.vanesline.sales.entity.Supplier;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderSpecification {

    public static Specification<PurchaseOrder> withCriteria(SearchPurchaseOrderRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getShopId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shopId"), request.getShopId()));
            }

            if (request.getSupplierId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("supplierId"), request.getSupplierId()));
            }

            if (StringUtils.hasText(request.getSupplierName())) {
                Subquery<java.util.UUID> supplierSubquery = query.subquery(java.util.UUID.class);
                Root<Supplier> supplierRoot = supplierSubquery.from(Supplier.class);
                supplierSubquery.select(supplierRoot.get("id"))
                        .where(criteriaBuilder.like(
                                criteriaBuilder.lower(supplierRoot.get("supplierName")),
                                "%" + request.getSupplierName().toLowerCase() + "%"
                        ));
                predicates.add(root.get("supplierId").in(supplierSubquery));
            }

            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
