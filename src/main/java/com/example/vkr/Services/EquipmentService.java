package com.example.vkr.Services;

import com.example.vkr.DTO.EquipmentFilterDTO;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Repositories.EquipmentRepository;
import com.example.vkr.Specifications.EquipmentSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    // Метод для получения отфильтрованных записей оборудования
    public List<Equipment> getFilteredEquipment(EquipmentFilterDTO filter) {
        // Использование спецификации для фильтрации
        return equipmentRepository.findAll(EquipmentSpecification.withFilters(filter)); // Без пагинации
    }

    public List<String> findDistinctTypes() {
        return equipmentRepository.findDistinctTypes();
    }

    public List<String> findDistinctLocations() {
        return equipmentRepository.findDistinctLocations();
    }

    public List<String> findDistinctStatuses() {
        return equipmentRepository.findDistinctStatuses();
    }

    public List<String> findDistinctSuppliers() {
        return equipmentRepository.findDistinctSuppliers();
    }

    public Optional<Equipment> getEquipmentById(Integer id) {
        return equipmentRepository.findById(id);
    }

    public Equipment saveEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipment(Integer id) {
        equipmentRepository.deleteById(id);
    }

    // Получение общей стоимости оборудования
    public double getTotalCost() {
        return equipmentRepository.findAll().stream()
                .mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0)
                .sum();
    }

    // Подсчет количества оборудования по статусу
    public long countByStatus(String status) {
        return equipmentRepository.findAll().stream()
                .filter(e -> status.equalsIgnoreCase(e.getStatus()))
                .count();
    }
}
