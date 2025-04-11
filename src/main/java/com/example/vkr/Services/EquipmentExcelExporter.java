package com.example.vkr.Services;

import com.example.vkr.Models.Equipment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class EquipmentExcelExporter {

    public static ByteArrayInputStream exportToExcel(List<Equipment> equipmentList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Оборудование");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Название", "Тип", "Модель", "Серийный номер", "Локация", "Дата закупки", "Гарантия до",
                    "Последнее ТО", "Следующее ТО", "Статус", "Поставщик", "Стоимость"};

            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
            }

            // Данные
            int rowIdx = 1;
            for (Equipment eq : equipmentList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(eq.getId());
                row.createCell(1).setCellValue(eq.getName());
                row.createCell(2).setCellValue(eq.getType());
                row.createCell(3).setCellValue(eq.getModel());
                row.createCell(4).setCellValue(eq.getSerialNumber());
                row.createCell(5).setCellValue(eq.getLocation());
                row.createCell(6).setCellValue(eq.getPurchaseDate() != null ? eq.getPurchaseDate().toString() : "");
                row.createCell(7).setCellValue(eq.getWarrantyExpiration() != null ? eq.getWarrantyExpiration().toString() : "");
                row.createCell(8).setCellValue(eq.getLastMaintenance() != null ? eq.getLastMaintenance().toString() : "");
                row.createCell(9).setCellValue(eq.getNextMaintenance() != null ? eq.getNextMaintenance().toString() : "");
                row.createCell(10).setCellValue(eq.getStatus());
                row.createCell(11).setCellValue(eq.getSupplier());
                row.createCell(12).setCellValue(eq.getCost() != null ? eq.getCost() : 0.0);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}

