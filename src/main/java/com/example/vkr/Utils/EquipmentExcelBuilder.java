package com.example.vkr.Utils;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.DTO.AnalyticsInfoDTO;
import com.example.vkr.Models.Equipment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EquipmentExcelBuilder {

    public static ByteArrayInputStream exportToExcel(
            List<Equipment> equipmentList,
            List<String> columns,
            String chartType,
            String groupByField,
            String valueField,
            String subGroupByField,
            List<AnalyticsInfoDTO> analytics
    ) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet dataSheet = workbook.createSheet("Оборудование");
            fillEquipmentData(workbook, dataSheet, equipmentList, columns, analytics);

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
        return exportToExcel(equipmentList, columns, null, null, null, null, null);
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
            Map<String, Map<String, Double>> groupedData = ChartDataUtil.generateStackedChartData(
                    equipmentList, groupByField, subGroupByField, valueField);

            switch (chartType.toLowerCase()) {
                case "stacked":
                case "bar":
                    ExcelChartBuilder.createStackedBarChart((XSSFSheet) chartSheet, 1, 1, groupedData, title, xAxis, yAxis);
                    break;
                case "line":
                    ExcelChartBuilder.createLineChartWithGroups((XSSFSheet) chartSheet, 1, 1, groupedData, title, xAxis, yAxis);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип графика с группировкой: " + chartType);
            }
        } else {
            Map<String, Double> simpleData = ChartDataUtil.generateSimpleChartData(equipmentList, groupByField, valueField)
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            switch (chartType.toLowerCase()) {
                case "bar":
                    ExcelChartBuilder.createBarChart((XSSFSheet) chartSheet, 1, 1, simpleData, title, xAxis, yAxis);
                    break;
                case "pie":
                    ExcelChartBuilder.createPieChart((XSSFSheet) chartSheet, 1, 1, simpleData, title);
                    break;
                case "line":
                    ExcelChartBuilder.createLineChart((XSSFSheet) chartSheet, 1, 1, simpleData, title, xAxis, yAxis);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип графика: " + chartType);
            }
        }
    }

    private static void fillEquipmentData(
            Workbook workbook,
            Sheet sheet,
            List<Equipment> equipmentList,
            List<String> columns,
            List<AnalyticsInfoDTO> analytics
    ) {
        CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
        CellStyle dateStyle = ExcelStyleUtil.createDateStyle(workbook);
        CellStyle numberStyle = ExcelStyleUtil.createNumberStyle(workbook);

        List<String> columnsToExport = columns.stream()
                .filter(col -> EquipmentColumnsConfig.COLUMN_MAPPERS.containsKey(col)
                        || EquipmentColumnsConfig.ANALYTICS_DISPLAY_NAMES.containsKey(col))
                .collect(Collectors.toList());

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnsToExport.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(EquipmentFieldUtil.getFieldDisplayName(columnsToExport.get(i)));
            cell.setCellStyle(headerStyle);
        }

        CellStyle criticalStyle = ExcelStyleUtil.createCriticalStyle(workbook);
        CellStyle warningStyle = ExcelStyleUtil.createWarningStyle(workbook);
        CellStyle okStyle = ExcelStyleUtil.createOkStyle(workbook);
        CellStyle mediumStyle = ExcelStyleUtil.createMediumStyle(workbook);
        CellStyle highStyle = ExcelStyleUtil.createHighStyle(workbook);

        Map<Integer, Map<String, AnalyticsInfoDTO>> analyticsMap = new HashMap<>();
        if (analytics != null) {
            for (AnalyticsInfoDTO dto : analytics) {
                analyticsMap
                        .computeIfAbsent(dto.getEquipmentId(), k -> new HashMap<>())
                        .put(dto.getType(), dto);
            }
        }

        for (int i = 0; i < equipmentList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Equipment eq = equipmentList.get(i);
            Integer equipmentId = eq.getId();
            Map<String, AnalyticsInfoDTO> dtoMap = (equipmentId != null)
                    ? analyticsMap.getOrDefault(equipmentId, Collections.emptyMap())
                    : Collections.emptyMap();

            for (int j = 0; j < columnsToExport.size(); j++) {
                String columnName = columnsToExport.get(j);
                Object value;

                if (EquipmentColumnsConfig.COLUMN_MAPPERS.containsKey(columnName)) {
                    value = EquipmentColumnsConfig.COLUMN_MAPPERS.get(columnName).apply(eq);
                } else if (columnName.equals("remainingWarrantyYears") || columnName.equals("remainingWarrantyComment")) {
                    AnalyticsInfoDTO dto = dtoMap.get("warranty");
                    value = (dto != null)
                            ? (columnName.endsWith("Years") ? dto.getNumericValue() : dto.getFormattedComment())
                            : "";
                } else if (columnName.equals("remainingMaintenanceYears") || columnName.equals("remainingMaintenanceComment")) {
                    AnalyticsInfoDTO dto = dtoMap.get("maintenance");
                    value = (dto != null)
                            ? (columnName.endsWith("Years") ? dto.getNumericValue() : dto.getFormattedComment())
                            : "";
                } else {
                    value = "";
                }

                Cell cell = row.createCell(j);
                ExcelStyleUtil.setCellValue(cell, value, dateStyle, numberStyle);

                // Стилизация wearLevel
                if ("wearLevel".equals(columnName) && value instanceof String) {
                    switch ((String) value) {
                        case "Низкий" -> cell.setCellStyle(okStyle);
                        case "Средний" -> cell.setCellStyle(mediumStyle);
                        case "Высокий" -> cell.setCellStyle(highStyle);
                        case "Критический" -> cell.setCellStyle(criticalStyle);
                    }
                }

                if (columnName.endsWith("Years") && value instanceof Number) {
                    double remaining = ((Number) value).doubleValue();
                    if (remaining <= 0) {
                        cell.setCellStyle(criticalStyle); // красный
                    } else if (remaining < 0.5) {
                        cell.setCellStyle(highStyle);     // оранжевый
                    } else if (remaining < 2) {
                        cell.setCellStyle(warningStyle);  // жёлтый
                    } else {
                        cell.setCellStyle(okStyle);       // зелёный
                    }
                }
            }
        }

        for (int i = 0; i < columnsToExport.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        if (columnsToExport.contains("remainingWarrantyYears")) {
            int legendStartRow = sheet.getLastRowNum() + 2;

            String[][] warrantyLegend = {
                    {"Интерпретация поля \"До окончания гарантии\":"},
                    {">= 2 лет", "Зелёный (нормально)"},
                    {"0.5 – 2 года", "Жёлтый (внимание)"},
                    {"< 0.5 года", "Оранжевый (гарантия истекает)"},
                    {"0 лет", "Красный (гарантия истекла)"}
            };

            for (int r = 0; r < warrantyLegend.length; r++) {
                Row row = sheet.createRow(legendStartRow + r);
                for (int c = 0; c < warrantyLegend[r].length; c++) {
                    row.createCell(c).setCellValue(warrantyLegend[r][c]);
                }
            }
        }
        if (columnsToExport.contains("remainingMaintenanceYears")) {
            int legendStartRow = sheet.getLastRowNum() + 2;

            String[][] maintenanceLegend = {
                    {"Интерпретация поля \"До следующего ТО\":"},
                    {">= 90 дней", "Зелёный (в пределах нормы)"},
                    {"30 – 89 дней", "Жёлтый (подходит срок)"},
                    {"1 – 29 дней", "Оранжевый (очень скоро)"},
                    {"<= 0 дней", "Красный (просрочено)"}
            };

            for (int r = 0; r < maintenanceLegend.length; r++) {
                Row row = sheet.createRow(legendStartRow + r);
                for (int c = 0; c < maintenanceLegend[r].length; c++) {
                    row.createCell(c).setCellValue(maintenanceLegend[r][c]);
                }
            }
        }
    }
}
