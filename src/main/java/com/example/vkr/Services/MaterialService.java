package com.example.vkr.Services;

import com.example.vkr.Models.Material;
import com.example.vkr.Repositories.MaterialRepository;
import com.example.vkr.Specifications.MaterialSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<Material> getAllMaterials(
            String name,
            Double quantity,
            String unit,
            String supplier,
            LocalDate arrivalDate,
            LocalDate expirationDate,
            String batchNumber) {

        Specification<Material> spec = MaterialSpecifications.withFilters(
                StringUtils.hasText(name) ? name : null,
                quantity,
                StringUtils.hasText(unit) ? unit : null,
                StringUtils.hasText(supplier) ? supplier : null,
                arrivalDate,
                expirationDate,
                StringUtils.hasText(batchNumber) ? batchNumber : null
        );

        return materialRepository.findAll(spec);
    }

    // Методы для получения уникальных значений для фильтров
    public List<String> getAllMaterialNames() {
        return materialRepository.findDistinctNames();
    }

    public List<Double> getAllMaterialQuantities() {
        return materialRepository.findDistinctQuantities();
    }

    public List<String> getAllMaterialUnits() {
        return materialRepository.findDistinctUnits();
    }

    public List<String> getAllMaterialSuppliers() {
        return materialRepository.findDistinctSuppliers();
    }

    public List<LocalDate> getAllMaterialArrivalDates() {
        return materialRepository.findDistinctArrivalDates();
    }

    public List<LocalDate> getAllMaterialExpirationDates() {
        return materialRepository.findDistinctExpirationDates();
    }

    public List<String> getAllMaterialBatchNumbers() {
        return materialRepository.findDistinctBatchNumbers();
    }

    // Другие методы сервиса
    public Optional<Material> getMaterialById(Integer id) {
        return materialRepository.findById(id);
    }

    public Material saveMaterial(Material material) {
        return materialRepository.save(material);
    }

    public void deleteMaterial(Integer id) {
        materialRepository.deleteById(id);
    }
}