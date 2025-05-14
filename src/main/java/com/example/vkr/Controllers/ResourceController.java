package com.example.vkr.Controllers;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.DTO.AnalyticsInfoDTO;
import com.example.vkr.DTO.EquipmentFilterDTO;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Services.*;
import com.example.vkr.Utils.EquipmentAnalyticsUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private ChartService chartService;

    @Autowired
    private EquipmentExcelService equipmentExcelService;

    @Autowired
    private EquipmentPdfService equipmentPdfService;

    @GetMapping
    public String showResourcesPage(@ModelAttribute("equipmentFilterDTO") EquipmentFilterDTO equipmentFilterDTO,
                                    Model model) {
        model.addAttribute("equipmentList", equipmentService.getFilteredEquipment(equipmentFilterDTO));
        model.addAttribute("allColumns", EquipmentColumnsConfig.COLUMN_DISPLAY_NAMES); // ← Передаем всю Map, а не values()
        model.addAttribute("uniqueTypes", equipmentService.findDistinctTypes());
        model.addAttribute("uniqueLocations", equipmentService.findDistinctLocations());
        model.addAttribute("uniqueStatuses", equipmentService.findDistinctStatuses());
        model.addAttribute("uniqueSuppliers", equipmentService.findDistinctSuppliers());
        return "resources";
    }

    @GetMapping("/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData(@ModelAttribute EquipmentFilterDTO filter,
                                            @RequestParam String groupByField,
                                            @RequestParam(required = false) String subGroupByField,
                                            @RequestParam String valueField) {
        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);

        if (subGroupByField != null && !subGroupByField.isBlank()) {
            return chartService.generateGroupedStackedChartData(filteredEquipment, groupByField, subGroupByField, valueField);
        } else {
            return chartService.generateSimpleChartData(filteredEquipment, groupByField, valueField);
        }
    }

    @GetMapping("/equipment/export-excel")
    public ResponseEntity<byte[]> exportToExcel(@ModelAttribute EquipmentFilterDTO filter,
                                                @RequestParam(required = false) List<String> columns,
                                                @RequestParam(required = false) String chartType,
                                                @RequestParam(required = false) String groupByField,
                                                @RequestParam(required = false) String valueField,
                                                @RequestParam(required = false) String subGroupByField,
                                                @RequestParam(required = false) List<String> analyticsFields) throws IOException {

        List<Equipment> equipmentList = equipmentService.getFilteredEquipment(filter);

        List<AnalyticsInfoDTO> analytics = new ArrayList<>();
        if (analyticsFields != null) {
            if (analyticsFields.contains("warranty")) {
                analytics.addAll(EquipmentAnalyticsUtil.buildWarrantyAnalytics(equipmentList));
            }
            // Добавить другие типы по мере появления
        }

        List<String> exportColumns;
        if (columns != null) {
            exportColumns = columns;
        } else {
            exportColumns = new ArrayList<>();
            exportColumns.addAll(EquipmentColumnsConfig.COLUMN_MAPPERS.keySet());
            exportColumns.addAll(EquipmentColumnsConfig.ANALYTICS_DISPLAY_NAMES.keySet());
        }

        byte[] excelData = equipmentExcelService.export(
                equipmentList,
                exportColumns,
                chartType,
                groupByField,
                valueField,
                subGroupByField,
                analytics
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=equipment_report.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelData.length)
                .body(excelData);
    }


    @GetMapping("/equipment/export-pdf")
    public void exportEquipmentToPdf(HttpServletResponse response,
                                     @ModelAttribute EquipmentFilterDTO filter,
                                     @RequestParam(required = false) List<String> columns) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=equipment.pdf");

        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);
        if (columns == null || columns.isEmpty()) {
            columns = new ArrayList<>(EquipmentColumnsConfig.COLUMN_DISPLAY_NAMES.keySet());
        }

        try (OutputStream out = response.getOutputStream()) {
            equipmentPdfService.export(filteredEquipment, columns, out);
        } catch (Exception e) {
            response.reset();
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Ошибка генерации PDF: " + e.getMessage());
            response.getWriter().flush();
        }
    }
}