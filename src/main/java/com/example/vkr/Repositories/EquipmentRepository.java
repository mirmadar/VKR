package com.example.vkr.Repositories;

import com.example.vkr.Models.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer>, JpaSpecificationExecutor<Equipment> {
    // остальные методы
}

