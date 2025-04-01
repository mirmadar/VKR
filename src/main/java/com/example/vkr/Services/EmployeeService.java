package com.example.vkr.Services;

import com.example.vkr.Models.Employee;
import com.example.vkr.Repositories.EmployeeRepository;
import com.example.vkr.Specifications.EmployeeSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees(
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

        Specification<Employee> spec = EmployeeSpecifications.withFilters(
                StringUtils.hasText(name) ? name : null,
                StringUtils.hasText(position) ? position : null,
                StringUtils.hasText(department) ? department : null,
                hireDate,
                salary,
                workload,
                overtimeHours,
                projectsCount,
                StringUtils.hasText(vacationStatus) ? vacationStatus : null,
                vacationDaysLeft,
                sickLeaveDays
        );

        return employeeRepository.findAll(spec);
    }

    // Методы для получения уникальных значений для фильтров
    public List<String> getAllEmployeeNames() {
        return employeeRepository.findDistinctNames();
    }

    public List<String> getAllEmployeePositions() {
        return employeeRepository.findDistinctPositions();
    }

    public List<String> getAllEmployeeDepartments() {
        return employeeRepository.findDistinctDepartments();
    }

    public List<LocalDate> getAllEmployeeHireDates() {
        return employeeRepository.findDistinctHireDates();
    }

    public List<Double> getAllEmployeeSalaries() {
        return employeeRepository.findDistinctSalaries();
    }

    public List<Double> getAllEmployeeWorkloads() {
        return employeeRepository.findDistinctWorkloads();
    }

    public List<Double> getAllEmployeeOvertimeHours() {
        return employeeRepository.findDistinctOvertimeHours();
    }

    public List<Integer> getAllEmployeeProjectsCounts() {
        return employeeRepository.findDistinctProjectsCounts();
    }

    public List<String> getAllEmployeeVacationStatuses() {
        return employeeRepository.findDistinctVacationStatuses();
    }

    public List<Integer> getAllEmployeeVacationDaysLeft() {
        return employeeRepository.findDistinctVacationDaysLeft();
    }

    public List<Integer> getAllEmployeeSickLeaveDays() {
        return employeeRepository.findDistinctSickLeaveDays();
    }

    // Другие методы сервиса
    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }
}