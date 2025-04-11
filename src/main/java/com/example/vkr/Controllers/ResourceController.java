package com.example.vkr.Controllers;

import com.example.vkr.DTO.EquipmentFilterDTO;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Services.EquipmentExcelExporter;
import com.example.vkr.Services.EquipmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/resources")
@AllArgsConstructor
public class ResourceController {

    private final EquipmentService equipmentService;

    @ModelAttribute("equipmentFilterDTO")
    public EquipmentFilterDTO equipmentFilterDTO() {
        return new EquipmentFilterDTO();
    }

    @GetMapping
    public String showResourcesPage(@ModelAttribute("equipmentFilterDTO") EquipmentFilterDTO equipmentFilterDTO, Model model) {
        List<Equipment> equipmentList = equipmentService.getFilteredEquipment(equipmentFilterDTO);
        model.addAttribute("equipmentList", equipmentList);
        return "resources";
    }

    @GetMapping("/equipment/export")
    public ResponseEntity<byte[]> exportEquipmentToExcel(@ModelAttribute("equipmentFilterDTO") EquipmentFilterDTO filter) throws IOException {
        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);
        ByteArrayInputStream in = EquipmentExcelExporter.exportToExcel(filteredEquipment);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=equipment.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }
}
