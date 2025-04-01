package com.example.vkr.Repositories;

import com.example.vkr.Models.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer>, JpaSpecificationExecutor<Equipment> {

    @Query("SELECT DISTINCT e.name FROM Equipment e")
    List<String> findDistinctNames();

    @Query("SELECT DISTINCT e.type FROM Equipment e")
    List<String> findDistinctTypes();

    @Query("SELECT DISTINCT e.location FROM Equipment e")
    List<String> findDistinctLocations();

    @Query("SELECT DISTINCT e.condition FROM Equipment e")
    List<String> findDistinctConditions();

    @Query("SELECT DISTINCT e.dateOfVerification FROM Equipment e WHERE e.dateOfVerification IS NOT NULL")
    List<LocalDate> findDistinctVerificationDates();

    @Query("SELECT DISTINCT e.lastMaintenanceDate FROM Equipment e WHERE e.lastMaintenanceDate IS NOT NULL")
    List<LocalDate> findDistinctLastMaintenanceDates();

    @Query("SELECT DISTINCT e.nextMaintenanceDate FROM Equipment e WHERE e.nextMaintenanceDate IS NOT NULL")
    List<LocalDate> findDistinctNextMaintenanceDates();
}