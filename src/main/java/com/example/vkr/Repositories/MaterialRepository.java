package com.example.vkr.Repositories;

import com.example.vkr.Models.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer>, JpaSpecificationExecutor<Material> {

    @Query("SELECT DISTINCT m.name FROM Material m")
    List<String> findDistinctNames();

    @Query("SELECT DISTINCT m.quantity FROM Material m")
    List<Double> findDistinctQuantities();

    @Query("SELECT DISTINCT m.unit FROM Material m")
    List<String> findDistinctUnits();

    @Query("SELECT DISTINCT m.supplier FROM Material m")
    List<String> findDistinctSuppliers();

    @Query("SELECT DISTINCT m.arrivalDate FROM Material m WHERE m.arrivalDate IS NOT NULL")
    List<LocalDate> findDistinctArrivalDates();

    @Query("SELECT DISTINCT m.expirationDate FROM Material m WHERE m.expirationDate IS NOT NULL")
    List<LocalDate> findDistinctExpirationDates();

    @Query("SELECT DISTINCT m.batchNumber FROM Material m WHERE m.batchNumber IS NOT NULL")
    List<String> findDistinctBatchNumbers();
}