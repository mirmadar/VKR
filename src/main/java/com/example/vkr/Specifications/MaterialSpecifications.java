package com.example.vkr.Specifications;

import com.example.vkr.Models.Material;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaterialSpecifications {
    public static Specification<Material> withFilters(
            String name,
            Double quantity,
            String unit,
            String supplier,
            LocalDate arrivalDate,
            LocalDate expirationDate,
            String batchNumber) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (quantity != null) {
                predicates.add(cb.equal(root.get("quantity"), quantity));
            }
            if (StringUtils.hasText(unit)) {
                predicates.add(cb.equal(root.get("unit"), unit));
            }
            if (StringUtils.hasText(supplier)) {
                predicates.add(cb.equal(root.get("supplier"), supplier));
            }
            if (arrivalDate != null) {
                predicates.add(cb.equal(root.get("arrivalDate"), arrivalDate));
            }
            if (expirationDate != null) {
                predicates.add(cb.equal(root.get("expirationDate"), expirationDate));
            }
            if (StringUtils.hasText(batchNumber)) {
                predicates.add(cb.equal(root.get("batchNumber"), batchNumber));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}