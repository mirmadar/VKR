package com.example.vkr.Repositories;

import com.example.vkr.Models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    @Query("SELECT DISTINCT e.name FROM Employee e")
    List<String> findDistinctNames();

    @Query("SELECT DISTINCT e.position FROM Employee e")
    List<String> findDistinctPositions();

    @Query("SELECT DISTINCT e.department FROM Employee e")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT e.hireDate FROM Employee e WHERE e.hireDate IS NOT NULL")
    List<LocalDate> findDistinctHireDates();

    @Query("SELECT DISTINCT e.salary FROM Employee e")
    List<Double> findDistinctSalaries();

    @Query("SELECT DISTINCT e.workload FROM Employee e")
    List<Double> findDistinctWorkloads();

    @Query("SELECT DISTINCT e.overtimeHours FROM Employee e")
    List<Double> findDistinctOvertimeHours();

    @Query("SELECT DISTINCT e.projectsCount FROM Employee e")
    List<Integer> findDistinctProjectsCounts();

    @Query("SELECT DISTINCT e.vacationStatus FROM Employee e WHERE e.vacationStatus IS NOT NULL")
    List<String> findDistinctVacationStatuses();

    @Query("SELECT DISTINCT e.vacationDaysLeft FROM Employee e")
    List<Integer> findDistinctVacationDaysLeft();

    @Query("SELECT DISTINCT e.sickLeaveDays FROM Employee e")
    List<Integer> findDistinctSickLeaveDays();
}