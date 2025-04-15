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
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

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
            @ModelAttribute EquipmentFilterDTO filter,
            @RequestParam(required = false) List<String> columns,
            @RequestParam(required = false) String chartType,
            @RequestParam(required = false) String groupByField,
            @RequestParam(required = false) String valueField) throws IOException {

        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);

        // Если не указаны колонки, используем все доступные
        if (columns == null || columns.isEmpty()) {
            columns = new ArrayList<>(EquipmentColumnsConfig.COLUMN_NAMES);
        }

        ByteArrayInputStream in = EquipmentExcelExporter.exportToExcel(
                filteredEquipment,
                columns,
                chartType,
                groupByField,
                valueField
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=equipment.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }

    @GetMapping("/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData(
            @ModelAttribute EquipmentFilterDTO filter,
            @RequestParam String groupByField,
            @RequestParam String valueField) {

        List<Equipment> filteredData = equipmentService.getFilteredEquipment(filter);
        return generateChartData(filteredData, groupByField, valueField);
    }

    private Map<String, Object> generateChartData(List<Equipment> equipment,
                                                  String groupByField,
                                                  String valueField) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Группировка данных
        Map<String, Double> groupedData = equipment.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        e -> getFieldValue(e, groupByField),
                        Collectors.summingDouble(e -> getNumericFieldValue(e, valueField))
                ));

        // Сортировка по значению (по убыванию)
        List<Map.Entry<String, Double>> sortedEntries = groupedData.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Подготовка результата
        result.put("labels", sortedEntries.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        result.put("values", sortedEntries.stream().map(Map.Entry::getValue).collect(Collectors.toList()));
        result.put("title", String.format("%s по %s",
                getFieldDisplayName(valueField),
                getFieldDisplayName(groupByField)));
        result.put("datasetLabel", getFieldDisplayName(valueField));

        return result;
    }

    private String getFieldValue(Equipment e, String field) {
        if (e == null || field == null) {
            return "Не указано";
        }

        switch (field.toLowerCase()) {
            case "type": return e.getType() != null ? e.getType() : "Не указано";
            case "location": return e.getLocation() != null ? e.getLocation() : "Не указано";
            case "status": return e.getStatus() != null ? e.getStatus() : "Не указано";
            case "supplier": return e.getSupplier() != null ? e.getSupplier() : "Не указано";
            case "purchaseyear":
                return e.getPurchaseDate() != null ?
                        String.valueOf(e.getPurchaseDate().getYear()) : "Не указано";
            default: return "Неизвестное поле";
        }
    }

    private double getNumericFieldValue(Equipment e, String field) {
        if (e == null || field == null) {
            return 0;
        }

        switch (field.toLowerCase()) {
            case "cost": return e.getCost() != null ? e.getCost() : 0;
            default: return 1; // Для подсчета количества
        }
    }

    private String getFieldDisplayName(String field) {
        if (field == null) {
            return "Неизвестное поле";
        }

        switch (field.toLowerCase()) {
            case "type": return "Тип оборудования";
            case "location": return "Локация";
            case "status": return "Статус";
            case "supplier": return "Поставщик";
            case "cost": return "Стоимость";
            case "purchaseyear": return "Год покупки";
            default: return field;
        }
    }

    @GetMapping("/equipment/export-pdf")
    public void exportEquipmentToPdf(HttpServletResponse response,
                                     @ModelAttribute EquipmentFilterDTO filter,
                                     @RequestParam(required = false) List<String> columns) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=equipment.pdf");

        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);

        // Если не указаны колонки, используем все доступные
        if (columns == null || columns.isEmpty()) {
            columns = new ArrayList<>(EquipmentColumnsConfig.COLUMN_NAMES);
        }

        try (OutputStream out = response.getOutputStream()) {
            EquipmentPdfExporter exporter = new EquipmentPdfExporter(filteredEquipment, columns);
            exporter.export(out);
        } catch (Exception e) {
            response.reset();
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Ошибка генерации PDF: " + e.getMessage());
            response.getWriter().flush();
        }
    }
}