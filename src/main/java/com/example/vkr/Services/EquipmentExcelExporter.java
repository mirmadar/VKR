package com.example.vkr.Services;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Utils.ExcelChartBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import com.example.vkr.Utils.ExcelStyleUtil;
import com.example.vkr.Utils.EquipmentFieldUtil;

public class EquipmentExcelExporter {

    public static ByteArrayInputStream exportToExcel(
            List<Equipment> equipmentList,
            List<String> columns,
            String chartType,
            String groupByField,
            String valueField,
            String subGroupByField // для stacked
    ) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 1. Основной лист с данными
            Sheet dataSheet = workbook.createSheet("Оборудование");
            fillEquipmentData(workbook, dataSheet, equipmentList, columns);

            // 2. График
            if (chartType != null && groupByField != null && valueField != null) {
                createChartSheet(workbook, equipmentList, chartType, groupByField, valueField, subGroupByField);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public static ByteArrayInputStream exportToExcel(
            List<Equipment> equipmentList,
            List<String> columns
    ) throws IOException {
        return exportToExcel(equipmentList, columns, null, null, null, null);
    }

    private static void createChartSheet(
            Workbook workbook,
            List<Equipment> equipmentList,
            String chartType,
            String groupByField,
            String valueField,
            String subGroupByField
    ) {
        if (!(workbook instanceof XSSFWorkbook)) {
            throw new IllegalArgumentException("Для создания графиков требуется XSSFWorkbook");
        }

        Sheet chartSheet = workbook.createSheet("Аналитика");

        String title = "Анализ по " + EquipmentFieldUtil.getFieldDisplayName(groupByField) +
                (subGroupByField != null && !subGroupByField.isEmpty()
                        ? " и " + EquipmentFieldUtil.getFieldDisplayName(subGroupByField)
                        : "");
        String xAxis = EquipmentFieldUtil.getFieldDisplayName(groupByField);
        String yAxis = EquipmentFieldUtil.getValueDisplayName(valueField);

        if (subGroupByField != null && !subGroupByField.isEmpty()) {
            Map<String, Map<String, Double>> groupedData = generateStackedChartData(
                    equipmentList, groupByField, subGroupByField, valueField);

            switch (chartType.toLowerCase()) {
                case "stacked":
                case "bar":
                    ExcelChartBuilder.createStackedBarChart(
                            (XSSFSheet) chartSheet,
                            1, 1,
                            groupedData,
                            title,
                            xAxis,
                            yAxis
                    );
                    break;
                case "line":
                    ExcelChartBuilder.createLineChartWithGroups(
                            (XSSFSheet) chartSheet,
                            1, 1,
                            groupedData,
                            title,
                            xAxis,
                            yAxis
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип графика с группировкой: " + chartType);
            }
        } else {
            Map<String, Double> simpleData = generateSimpleChartData(equipmentList, groupByField, valueField);

            switch (chartType.toLowerCase()) {
                case "bar":
                    ExcelChartBuilder.createBarChart(
                            (XSSFSheet) chartSheet,
                            1, 1,
                            simpleData,
                            title,
                            xAxis,
                            yAxis
                    );
                    break;
                case "pie":
                    ExcelChartBuilder.createPieChart(
                            (XSSFSheet) chartSheet,
                            1, 1,
                            simpleData,
                            title
                    );
                    break;
                case "line":
                    ExcelChartBuilder.createLineChart(
                            (XSSFSheet) chartSheet,
                            1, 1,
                            simpleData,
                            title,
                            xAxis,
                            yAxis
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип графика: " + chartType);
            }
        }
    }

    private static Map<String, Map<String, Double>> generateStackedChartData(
            List<Equipment> equipmentList,
            String groupByField,
            String subGroupByField,
            String valueField
    ) {
        Map<String, Map<String, Double>> result = new LinkedHashMap<>();
        Set<String> allSubCategories = equipmentList.stream()
                .map(e -> String.valueOf(EquipmentFieldUtil.getGroupingValue(e, subGroupByField)))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        equipmentList.stream()
                .map(e -> String.valueOf(EquipmentFieldUtil.getGroupingValue(e, groupByField)))
                .distinct()
                .forEach(mainCat -> {
                    Map<String, Double> subMap = new LinkedHashMap<>();
                    allSubCategories.forEach(subCat -> subMap.put(subCat, 0.0));
                    result.put(mainCat, subMap);
                });

        for (Equipment equipment : equipmentList) {
            String mainCat = String.valueOf(EquipmentFieldUtil.getGroupingValue(equipment, groupByField));
            String subCat = String.valueOf(EquipmentFieldUtil.getGroupingValue(equipment, subGroupByField));
            Double value = EquipmentFieldUtil.getChartValue(equipment, valueField);
            result.get(mainCat).merge(subCat, value, Double::sum);
        }

        return result;
    }

    private static Map<String, Double> generateSimpleChartData(
            List<Equipment> equipmentList,
            String groupByField,
            String valueField
    ) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Equipment equipment : equipmentList) {
            String groupKey = String.valueOf(EquipmentFieldUtil.getGroupingValue(equipment, groupByField));
            Double value = EquipmentFieldUtil.getChartValue(equipment, valueField);
            result.merge(groupKey, value, Double::sum);
        }
        return result;
    }

    private static void fillEquipmentData(
            Workbook workbook,
            Sheet sheet,
            List<Equipment> equipmentList,
            List<String> columns
    ) {
        CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
        CellStyle dateStyle = ExcelStyleUtil.createDateStyle(workbook);
        CellStyle numberStyle = ExcelStyleUtil.createNumberStyle(workbook);

        List<String> columnsToExport = columns.stream()
                .filter(EquipmentColumnsConfig.COLUMN_MAPPERS::containsKey)
                .collect(Collectors.toList());

        if (columnsToExport.isEmpty()) {
            columnsToExport = new ArrayList<>(EquipmentColumnsConfig.COLUMN_MAPPERS.keySet());
        }

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnsToExport.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(EquipmentFieldUtil.getFieldDisplayName(columnsToExport.get(i)));
            cell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < equipmentList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Equipment eq = equipmentList.get(i);

            for (int j = 0; j < columnsToExport.size(); j++) {
                String columnName = columnsToExport.get(j);
                Object value = EquipmentColumnsConfig.COLUMN_MAPPERS.get(columnName).apply(eq);
                ExcelStyleUtil.setCellValue(row.createCell(j), value, dateStyle, numberStyle);
            }
        }

        for (int i = 0; i < columnsToExport.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
