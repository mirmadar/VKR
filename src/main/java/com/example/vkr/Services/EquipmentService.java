package com.example.vkr.Services;

import com.example.vkr.Models.Equipment;
import com.example.vkr.Repositories.EquipmentRepository;
import com.example.vkr.Specifications.EquipmentSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipment(
            String name,
            String type,
            String location,
            String condition,
            LocalDate verificationDate,
            LocalDate lastMaintenanceDate,
            LocalDate nextMaintenanceDate) {

        Specification<Equipment> spec = EquipmentSpecifications.withFilters(
                StringUtils.hasText(name) ? name : null,
                StringUtils.hasText(type) ? type : null,
                StringUtils.hasText(location) ? location : null,
                StringUtils.hasText(condition) ? condition : null,
                verificationDate,
                lastMaintenanceDate,
                nextMaintenanceDate
        );

        return equipmentRepository.findAll(spec);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String emptyToNull(String str) {
        return isEmpty(str) ? null : str;
    }

    public Optional<Equipment> getEquipmentById(int id) {
        return equipmentRepository.findById(id);
    }

    public Equipment saveEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipment(int id) {
        equipmentRepository.deleteById(id);
    }

    public List<String> getAllEquipmentNames() {
        return equipmentRepository.findDistinctNames();
    }

    public List<String> getAllEquipmentTypes() {
        return equipmentRepository.findDistinctTypes();
    }

    public List<String> getAllEquipmentLocations() {
        return equipmentRepository.findDistinctLocations();
    }

    public List<String> getAllEquipmentConditions() {
        return equipmentRepository.findDistinctConditions();
    }

    public List<LocalDate> getAllEquipmentVerificationDates() {
        return equipmentRepository.findDistinctVerificationDates();
    }

    public List<LocalDate> getAllEquipmentLastMaintenanceDates() {
        return equipmentRepository.findDistinctLastMaintenanceDates();
    }

    public List<LocalDate> getAllEquipmentNextMaintenanceDates() {
        return equipmentRepository.findDistinctNextMaintenanceDates();
    }
}

