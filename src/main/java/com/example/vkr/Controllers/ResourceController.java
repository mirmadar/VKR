package com.example.vkr.Controllers;

import com.example.vkr.Models.Employee;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Models.Material;
import com.example.vkr.Services.EmployeeService;
import com.example.vkr.Services.EquipmentService;
import com.example.vkr.Services.MaterialService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;


@Controller
public class ResourceController {

    private final EquipmentService equipmentService;
    private final MaterialService materialService;
    private final EmployeeService employeeService;

    public ResourceController(EquipmentService equipmentService, MaterialService materialService, EmployeeService employeeService) {
        this.equipmentService = equipmentService;
        this.materialService = materialService;
        this.employeeService = employeeService;
    }

    @GetMapping("/resources")
    public String getResourcesPage(
            @RequestParam(required = false) String equipmentName,
            @RequestParam(required = false) String equipmentType,
            @RequestParam(required = false) String equipmentLocation,
            @RequestParam(required = false) String equipmentCondition,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate equipmentVerificationDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate equipmentLastMaintenanceDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate equipmentNextMaintenanceDate,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String employeePosition,
            @RequestParam(required = false) String employeeDepartment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate employeeHireDate,
            @RequestParam(required = false) Double employeeSalary,
            @RequestParam(required = false) Double employeeWorkload,
            @RequestParam(required = false) Double employeeOvertimeHours,
            @RequestParam(required = false) Integer employeeProjectsCount,
            @RequestParam(required = false) String employeeVacationStatus,
            @RequestParam(required = false) Integer employeeVacationDaysLeft,
            @RequestParam(required = false) Integer employeeSickLeaveDays,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) Double materialQuantity,
            @RequestParam(required = false) String materialUnit,
            @RequestParam(required = false) String materialSupplier,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate materialArrivalDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate materialExpirationDate,
            @RequestParam(required = false) String materialBatchNumber,
            Model model) {

        // Получаем данные
        List<Equipment> equipmentList = equipmentService.getAllEquipment(
                equipmentName,
                equipmentType,
                equipmentLocation,
                equipmentCondition,
                equipmentVerificationDate,
                equipmentLastMaintenanceDate,
                equipmentNextMaintenanceDate
        );

        // Получаем уникальные значения для фильтров
        model.addAttribute("equipmentNames", equipmentService.getAllEquipmentNames());
        model.addAttribute("equipmentTypes", equipmentService.getAllEquipmentTypes());
        model.addAttribute("equipmentLocations", equipmentService.getAllEquipmentLocations());
        model.addAttribute("equipmentConditions", equipmentService.getAllEquipmentConditions());
        model.addAttribute("equipmentVerificationDates", equipmentService.getAllEquipmentVerificationDates());
        model.addAttribute("equipmentLastMaintenanceDates", equipmentService.getAllEquipmentLastMaintenanceDates());
        model.addAttribute("equipmentNextMaintenanceDates", equipmentService.getAllEquipmentNextMaintenanceDates());

        // Передаем выбранные фильтры
        model.addAttribute("selectedEquipmentName", equipmentName);
        model.addAttribute("selectedEquipmentType", equipmentType);
        model.addAttribute("selectedEquipmentLocation", equipmentLocation);
        model.addAttribute("selectedEquipmentCondition", equipmentCondition);
        model.addAttribute("selectedEquipmentVerificationDate", equipmentVerificationDate);
        model.addAttribute("selectedEquipmentLastMaintenanceDate", equipmentLastMaintenanceDate);
        model.addAttribute("selectedEquipmentNextMaintenanceDate", equipmentNextMaintenanceDate);

        model.addAttribute("equipmentList", equipmentList);

        List<Employee> employeeList = employeeService.getAllEmployees(
                employeeName,
                employeePosition,
                employeeDepartment,
                employeeHireDate,
                employeeSalary,
                employeeWorkload,
                employeeOvertimeHours,
                employeeProjectsCount,
                employeeVacationStatus,
                employeeVacationDaysLeft,
                employeeSickLeaveDays
        );

        // Добавляем атрибуты для фильтров сотрудников
        model.addAttribute("employeeNames", employeeService.getAllEmployeeNames());
        model.addAttribute("employeePositions", employeeService.getAllEmployeePositions());
        model.addAttribute("employeeDepartments", employeeService.getAllEmployeeDepartments());
        model.addAttribute("employeeHireDates", employeeService.getAllEmployeeHireDates());
        model.addAttribute("employeeSalaries", employeeService.getAllEmployeeSalaries());
        model.addAttribute("employeeWorkloads", employeeService.getAllEmployeeWorkloads());
        model.addAttribute("employeeOvertimeHours", employeeService.getAllEmployeeOvertimeHours());
        model.addAttribute("employeeProjectCounts", employeeService.getAllEmployeeProjectsCounts());
        model.addAttribute("employeeVacationStatuses", employeeService.getAllEmployeeVacationStatuses());
        model.addAttribute("employeeVacationDaysLeft", employeeService.getAllEmployeeVacationDaysLeft());
        model.addAttribute("employeeSickLeaveDays", employeeService.getAllEmployeeSickLeaveDays());

        model.addAttribute("selectedEmployeeName", employeeName);
        model.addAttribute("selectedEmployeePosition", employeePosition);
        model.addAttribute("selectedEmployeeDepartment", employeeDepartment);
        model.addAttribute("selectedEmployeeHireDate", employeeHireDate);
        model.addAttribute("selectedEmployeeSalary", employeeSalary);
        model.addAttribute("selectedEmployeeWorkload", employeeWorkload);
        model.addAttribute("selectedEmployeeOvertimeHours", employeeOvertimeHours);
        model.addAttribute("selectedEmployeeProjectsCount", employeeProjectsCount);
        model.addAttribute("selectedEmployeeVacationStatus", employeeVacationStatus);
        model.addAttribute("selectedEmployeeVacationDaysLeft", employeeVacationDaysLeft);
        model.addAttribute("selectedEmployeeSickLeaveDays", employeeSickLeaveDays);

        model.addAttribute("employeeList", employeeList);

        List<Material> materialList = materialService.getAllMaterials(
                materialName,
                materialQuantity,
                materialUnit,
                materialSupplier,
                materialArrivalDate,
                materialExpirationDate,
                materialBatchNumber
        );

        // Добавляем атрибуты для фильтров материалов
        model.addAttribute("materialNames", materialService.getAllMaterialNames());
        model.addAttribute("materialQuantities", materialService.getAllMaterialQuantities());
        model.addAttribute("materialUnits", materialService.getAllMaterialUnits());
        model.addAttribute("materialSuppliers", materialService.getAllMaterialSuppliers());
        model.addAttribute("materialArrivalDates", materialService.getAllMaterialArrivalDates());
        model.addAttribute("materialExpirationDates", materialService.getAllMaterialExpirationDates());
        model.addAttribute("materialBatchNumbers", materialService.getAllMaterialBatchNumbers());

        model.addAttribute("selectedMaterialName", materialName);
        model.addAttribute("selectedMaterialQuantity", materialQuantity);
        model.addAttribute("selectedMaterialUnit", materialUnit);
        model.addAttribute("selectedMaterialSupplier", materialSupplier);
        model.addAttribute("selectedMaterialArrivalDate", materialArrivalDate);
        model.addAttribute("selectedMaterialExpirationDate", materialExpirationDate);
        model.addAttribute("selectedMaterialBatchNumber", materialBatchNumber);

        model.addAttribute("materialList", materialList);

        return "resources";
    }
}
