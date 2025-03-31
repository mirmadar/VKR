package com.example.vkr.Controllers;

import com.example.vkr.Models.Employee;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Models.Material;
import com.example.vkr.Services.EmployeeService;
import com.example.vkr.Services.EquipmentService;
import com.example.vkr.Services.MaterialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String getResourcesPage(Model model) {
        List<Equipment> equipmentList = equipmentService.getAllEquipment();
        List<Material> materialsList = materialService.getAllMaterials();
        List<Employee> employeesList = employeeService.getAllEmployees();

        model.addAttribute("equipmentList", equipmentList);
        model.addAttribute("materialsList", materialsList);
        model.addAttribute("employeesList", employeesList);

        return "resources";
    }


}
