package com.example.vkr.Services;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.Models.Equipment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EquipmentExcelExporter {

    public static ByteArrayInputStream exportToExcel(List<Equipment> equipmentList, List<String> columns) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Стили для оформления
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            Sheet sheet = workbook.createSheet("Оборудование");

            // Фильтруем колонки
            List<String> columnsToExport = columns.stream()
                    .filter(EquipmentColumnsConfig.COLUMN_MAPPERS::containsKey)
                    .collect(Collectors.toList());

            if (columnsToExport.isEmpty()) {
                columnsToExport = new ArrayList<>(EquipmentColumnsConfig.COLUMN_MAPPERS.keySet());
            }

            // Создаем заголовки
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnsToExport.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnsToExport.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Заполняем данные
            for (int i = 0; i < equipmentList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Equipment eq = equipmentList.get(i);

                for (int j = 0; j < columnsToExport.size(); j++) {
                    String columnName = columnsToExport.get(j);
                    Object value = EquipmentColumnsConfig.COLUMN_MAPPERS.get(columnName).apply(eq);

                    Cell cell = row.createCell(j);
                    setCellValueWithStyle(cell, value, dateStyle, numberStyle);
                }
            }

            // Автоподбор ширины колонок
            for (int i = 0; i < columnsToExport.size(); i++) {
                sheet.autoSizeColumn(i);
                // Добавляем небольшой отступ
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true); // Жирный шрифт для заголовков
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.NONE);
        style.setBorderTop(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        style.setBorderLeft(BorderStyle.NONE);
        return style;
    }

    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat((short) 4); // Формат с 2 десятичными знаками
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private static void setCellValueWithStyle(Cell cell, Object value, CellStyle dateStyle, CellStyle numberStyle) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            cell.setCellStyle(dateStyle);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
            cell.setCellStyle(numberStyle);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}