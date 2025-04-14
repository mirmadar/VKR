package com.example.vkr.Controllers;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.DTO.EquipmentFilterDTO;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Services.EquipmentExcelExporter;
import com.example.vkr.Services.EquipmentPdfExporter;
import com.example.vkr.Services.EquipmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Controller
@RequestMapping("/resources")
@AllArgsConstructor
public class ResourceController {

    private final EquipmentService equipmentService;

    @GetMapping
    public String showResourcesPage(@ModelAttribute("equipmentFilterDTO") EquipmentFilterDTO equipmentFilterDTO,
                                    Model model) {
        model.addAttribute("equipmentList", equipmentService.getFilteredEquipment(equipmentFilterDTO));
        model.addAttribute("allColumns", EquipmentColumnsConfig.COLUMN_NAMES);

        model.addAttribute("uniqueTypes", equipmentService.findDistinctTypes());
        model.addAttribute("uniqueLocations", equipmentService.findDistinctLocations());
        model.addAttribute("uniqueStatuses", equipmentService.findDistinctStatuses());
        model.addAttribute("uniqueSuppliers", equipmentService.findDistinctSuppliers());
        return "resources";
    }

    @GetMapping("/equipment/export-excel")
    public ResponseEntity<byte[]> exportEquipmentToExcel(
            @ModelAttribute("equipmentFilterDTO") EquipmentFilterDTO filter,
            @RequestParam(value = "columns", required = false) List<String> columns) throws IOException {

        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);
        ByteArrayInputStream in = EquipmentExcelExporter.exportToExcel(filteredEquipment, columns);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=equipment.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }

    @GetMapping("/equipment/export-pdf")
    public void exportEquipmentToPdf(HttpServletResponse response,
                                     @ModelAttribute("equipmentFilterDTO") EquipmentFilterDTO filter,
                                     @RequestParam(required = false) List<String> columns) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=equipment.pdf");

        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);

        try (OutputStream out = response.getOutputStream()) {
            EquipmentPdfExporter exporter = new EquipmentPdfExporter(filteredEquipment, columns);
            exporter.export(out);
        } catch (Exception e) {
            response.reset();
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Ошибка генерации PDF: " + e.getMessage());
        }
    }

}