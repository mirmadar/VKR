package com.example.vkr.Specifications;

import com.example.vkr.Models.Equipment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipmentSpecifications {
    public static Specification<Equipment> withFilters(
            String name,
            String type,
            String location,
            String condition,
            LocalDate verificationDate,
            LocalDate lastMaintenanceDate,
            LocalDate nextMaintenanceDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (StringUtils.hasText(type)) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (StringUtils.hasText(location)) {
                predicates.add(cb.equal(root.get("location"), location));
            }
            if (StringUtils.hasText(condition)) {
                predicates.add(cb.equal(root.get("condition"), condition));
            }
            if (verificationDate != null) {
                predicates.add(cb.equal(root.get("dateOfVerification"), verificationDate));
            }
            if (lastMaintenanceDate != null) {
                predicates.add(cb.equal(root.get("lastMaintenanceDate"), lastMaintenanceDate));
            }
            if (nextMaintenanceDate != null) {
                predicates.add(cb.equal(root.get("nextMaintenanceDate"), nextMaintenanceDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
