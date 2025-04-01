package com.example.vkr.Specifications;

import com.example.vkr.Models.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecifications {
    public static Specification<Employee> withFilters(
            String name,
            String position,
            String department,
            LocalDate hireDate,
            Double salary,
            Double workload,
            Double overtimeHours,
            Integer projectsCount,
            String vacationStatus,
            Integer vacationDaysLeft,
            Integer sickLeaveDays) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (StringUtils.hasText(position)) {
                predicates.add(cb.equal(root.get("position"), position));
            }
            if (StringUtils.hasText(department)) {
                predicates.add(cb.equal(root.get("department"), department));
            }
            if (hireDate != null) {
                predicates.add(cb.equal(root.get("hireDate"), hireDate));
            }
            if (salary != null) {
                predicates.add(cb.equal(root.get("salary"), salary));
            }
            if (workload != null) {
                predicates.add(cb.equal(root.get("workload"), workload));
            }
            if (overtimeHours != null) {
                predicates.add(cb.equal(root.get("overtimeHours"), overtimeHours));
            }
            if (projectsCount != null) {
                predicates.add(cb.equal(root.get("projectsCount"), projectsCount));
            }
            if (StringUtils.hasText(vacationStatus)) {
                predicates.add(cb.equal(root.get("vacationStatus"), vacationStatus));
            }
            if (vacationDaysLeft != null) {
                predicates.add(cb.equal(root.get("vacationDaysLeft"), vacationDaysLeft));
            }
            if (sickLeaveDays != null) {
                predicates.add(cb.equal(root.get("sickLeaveDays"), sickLeaveDays));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
