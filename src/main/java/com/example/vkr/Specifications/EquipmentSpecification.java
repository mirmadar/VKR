package com.example.vkr.Specifications;

import com.example.vkr.DTO.EquipmentFilterDTO;
import com.example.vkr.Models.Equipment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class EquipmentSpecification {
    public static Specification<Equipment> withFilters(EquipmentFilterDTO filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Строковые поля
            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + filter.getName() + "%"));
            }
            if (filter.getType() != null && !filter.getType().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), filter.getType()));
            }
            if (filter.getModel() != null && !filter.getModel().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("model"), filter.getModel()));
            }
            if (filter.getSerialNumber() != null && !filter.getSerialNumber().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("serialNumber"), filter.getSerialNumber()));
            }
            if (filter.getLocation() != null && !filter.getLocation().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("location"), filter.getLocation()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getSupplier() != null && !filter.getSupplier().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("supplier"), filter.getSupplier()));
            }

            // Поля с датами
            if (filter.getPurchaseDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("purchaseDate"), filter.getPurchaseDateFrom()));
            }
            if (filter.getPurchaseDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("purchaseDate"), filter.getPurchaseDateTo()));
            }
            if (filter.getWarrantyExpirationFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("warrantyExpiration"), filter.getWarrantyExpirationFrom()));
            }
            if (filter.getWarrantyExpirationTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("warrantyExpiration"), filter.getWarrantyExpirationTo()));
            }
            if (filter.getLastMaintenanceFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("lastMaintenance"), filter.getLastMaintenanceFrom()));
            }
            if (filter.getLastMaintenanceTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("lastMaintenance"), filter.getLastMaintenanceTo()));
            }
            if (filter.getNextMaintenanceFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("nextMaintenance"), filter.getNextMaintenanceFrom()));
            }
            if (filter.getNextMaintenanceTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("nextMaintenance"), filter.getNextMaintenanceTo()));
            }

            // Числовые поля
            if (filter.getCostMin() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("cost"), filter.getCostMin()));
            }
            if (filter.getCostMax() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("cost"), filter.getCostMax()));
            }

            return predicate;
        };
    }
}
