package com.example.vkr.Repositories;

import com.example.vkr.Models.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer>, JpaSpecificationExecutor<Equipment> {
    @Query("SELECT DISTINCT e.type FROM Equipment e ORDER BY e.type")
    List<String> findDistinctTypes();

    @Query("SELECT DISTINCT e.location FROM Equipment e ORDER BY e.location")
    List<String> findDistinctLocations();

    @Query("SELECT DISTINCT e.status FROM Equipment e ORDER BY e.status")
    List<String> findDistinctStatuses();

    @Query("SELECT DISTINCT e.supplier FROM Equipment e ORDER BY e.supplier")
    List<String> findDistinctSuppliers();
}

