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
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(required = false) List<String> columns,
            @RequestParam(required = false) String chartType,
            @RequestParam(required = false) String groupByField,
            @RequestParam(required = false) String valueField,
            @RequestParam(required = false) String subGroupByField
    ) throws IOException {

        List<Equipment> equipmentList = equipmentService.getAllEquipment();

        ByteArrayInputStream byteArrayInputStream;

        if (chartType != null && groupByField != null && valueField != null) {
            byteArrayInputStream = EquipmentExcelExporter.exportToExcel(
                    equipmentList,
                    columns != null ? columns : List.of("type", "location", "status", "cost"),
                    chartType,
                    groupByField,
                    valueField,
                    subGroupByField
            );
        } else {
            byteArrayInputStream = EquipmentExcelExporter.exportToExcel(
                    equipmentList,
                    columns != null ? columns : List.of("type", "location", "status", "cost")
            );
        }

        byte[] excelData = byteArrayInputStream.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=equipment_report.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelData.length)
                .body(excelData);
    }

    @GetMapping("/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData(
            @ModelAttribute EquipmentFilterDTO filter,
            @RequestParam String groupByField,
            @RequestParam String valueField,
            @RequestParam(required = false) String subGroupByField,
            @RequestParam(defaultValue = "bar") String chartType) {

        List<Equipment> filteredData = equipmentService.getFilteredEquipment(filter);

        Map<String, Object> chartData;

        if (subGroupByField != null && !subGroupByField.isBlank()) {
            chartData = generateGroupedStackedChartData(filteredData, groupByField, subGroupByField, valueField);
        } else {
            chartData = generateChartData(filteredData, groupByField, valueField);
        }

        chartData.put("chartType", chartType);
        return chartData;
    }

    private Map<String, Object> generateGroupedStackedChartData(List<Equipment> equipment,
                                                                String groupByField,
                                                                String subGroupByField,
                                                                String valueField) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Map<String, Double>> grouped = new LinkedHashMap<>();

        for (Equipment e : equipment) {
            String mainKey = getFieldValue(e, groupByField);
            String subKey = getFieldValue(e, subGroupByField);
            double value = getNumericFieldValue(e, valueField);

            grouped.computeIfAbsent(subKey, k -> new LinkedHashMap<>())
                    .merge(mainKey, value, Double::sum);
        }

        Set<String> allMainKeys = grouped.values().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Map<String, Object>> datasets = new ArrayList<>();
        for (Map.Entry<String, Map<String, Double>> entry : grouped.entrySet()) {
            String subGroup = entry.getKey();
            Map<String, Double> subData = entry.getValue();

            List<Double> values = allMainKeys.stream()
                    .map(k -> subData.getOrDefault(k, 0.0))
                    .collect(Collectors.toList());

            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", subGroup);
            dataset.put("data", values);
            dataset.put("backgroundColor", getRandomColor());

            datasets.add(dataset);
        }

        result.put("labels", new ArrayList<>(allMainKeys));
        result.put("datasets", datasets);
        result.put("title", String.format("%s по %s и %s",
                getFieldDisplayName(valueField),
                getFieldDisplayName(groupByField),
                getFieldDisplayName(subGroupByField)));

        return result;
    }

    private Map<String, Object> generateChartData(List<Equipment> equipment,
                                                  String groupByField,
                                                  String valueField) {
        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, Double> groupedData = equipment.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        e -> getFieldValue(e, groupByField),
                        Collectors.summingDouble(e -> getNumericFieldValue(e, valueField))
                ));

        List<Map.Entry<String, Double>> sortedEntries = groupedData.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        result.put("labels", sortedEntries.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        result.put("values", sortedEntries.stream().map(Map.Entry::getValue).collect(Collectors.toList()));
        result.put("title", String.format("%s по %s",
                getFieldDisplayName(valueField),
                getFieldDisplayName(groupByField)));
        result.put("datasetLabel", getFieldDisplayName(valueField));

        return result;
    }

    private String getFieldValue(Equipment e, String field) {
        if (e == null || field == null) return "Не указано";

        return switch (field.toLowerCase()) {
            case "type" -> e.getType() != null ? e.getType() : "Не указано";
            case "location" -> e.getLocation() != null ? e.getLocation() : "Не указано";
            case "status" -> e.getStatus() != null ? e.getStatus() : "Не указано";
            case "supplier" -> e.getSupplier() != null ? e.getSupplier() : "Не указано";
            case "purchaseyear" -> e.getPurchaseDate() != null ? String.valueOf(e.getPurchaseDate().getYear()) : "Не указано";
            default -> "Неизвестное поле";
        };
    }

    private double getNumericFieldValue(Equipment e, String field) {
        if (e == null || field == null) return 0;

        return switch (field.toLowerCase()) {
            case "cost" -> e.getCost() != null ? e.getCost() : 0;
            default -> 1;
        };
    }

    private String getFieldDisplayName(String field) {
        if (field == null) return "Неизвестное поле";

        return switch (field.toLowerCase()) {
            case "type" -> "Тип оборудования";
            case "location" -> "Локация";
            case "status" -> "Статус";
            case "supplier" -> "Поставщик";
            case "cost" -> "Стоимость";
            case "count" -> "Количество";
            case "purchaseyear" -> "Год покупки";
            default -> field;
        };
    }

    private String getRandomColor() {
        String[] colors = {
                "#F3CAE2", "#A0D8EF", "#B0E57C", "#FFD580",
                "#CBAACB", "#FFB7B2", "#FFDAC1", "#B5EAD7",
                "#E2F0CB", "#FF9AA2", "#FFB347", "#77DD77"
        };
        return colors[new Random().nextInt(colors.length)];
    }

    @GetMapping("/equipment/export-pdf")
    public void exportEquipmentToPdf(HttpServletResponse response,
                                     @ModelAttribute EquipmentFilterDTO filter,
                                     @RequestParam(required = false) List<String> columns) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=equipment.pdf");

        List<Equipment> filteredEquipment = equipmentService.getFilteredEquipment(filter);
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
