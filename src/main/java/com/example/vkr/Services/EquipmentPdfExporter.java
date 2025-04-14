package com.example.vkr.Services;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.Models.Equipment;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.text.Document;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EquipmentPdfExporter {
    private final List<Equipment> equipmentList;
    private final List<String> columns;

    // Конструктор с параметрами
    public EquipmentPdfExporter(List<Equipment> equipmentList, List<String> columns) {
        this.equipmentList = (equipmentList != null) ? equipmentList : Collections.emptyList();
        if (columns == null || columns.isEmpty()) {
            this.columns = getDefaultColumns();
        } else {
            this.columns = columns;
        }
    }

    private static final Font FONT_HEADER;
    private static final Font FONT_DATA;

    static {
        try {
            BaseFont baseFont = BaseFont.createFont(
                    "static/freesans.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            FONT_HEADER = new Font(baseFont, 12, Font.BOLD);
            FONT_DATA = new Font(baseFont, 10);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize fonts", e);
        }
    }

    public void export(OutputStream outputStream) throws IOException {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Добавляем заголовок
            Paragraph title = new Paragraph("Отчет по оборудованию", FONT_HEADER);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            if (equipmentList.isEmpty()) {
                document.add(new Paragraph("Нет данных для отображения", FONT_DATA));
            } else {
                PdfPTable table = new PdfPTable(columns.size());
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                // Заголовки таблицы
                for (String col : columns) {
                    PdfPCell cell = new PdfPCell(new Phrase(getHeader(col), FONT_HEADER));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(new BaseColor(220, 220, 220));
                    table.addCell(cell);
                }

                // Данные таблицы
                for (Equipment eq : equipmentList) {
                    for (String col : columns) {
                        String value = getValue(eq, col);
                        PdfPCell cell = new PdfPCell(new Phrase(value, FONT_DATA));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);
                    }
                }

                document.add(table);
            }
        } catch (Exception e) {
        e.printStackTrace(); // логируем ошибку на сервере
        throw new RuntimeException("Ошибка генерации PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private String getValue(Equipment eq, String column) {
        Function<Equipment, Object> mapper = EquipmentColumnsConfig.COLUMN_MAPPERS.get(column);
        if (mapper == null) return "";
        Object value = mapper.apply(eq);
        if (value instanceof LocalDate) {
            return format((LocalDate) value);
        }
        if (value instanceof Number) {
            return String.format("%.2f", ((Number) value).doubleValue());
        }
        return value != null ? value.toString() : "";
    }

    private String getHeader(String column) {
        switch (column) {
            case "id": return "ID";
            case "name": return "Название";
            case "type": return "Тип";
            case "model": return "Модель";
            case "serialNumber": return "Серийный номер";
            case "location": return "Локация";
            case "purchaseDate": return "Дата закупки";
            case "warrantyExpiration": return "Гарантия до";
            case "lastMaintenance": return "Последнее ТО";
            case "nextMaintenance": return "Следующее ТО";
            case "status": return "Состояние";
            case "supplier": return "Поставщик";
            case "cost": return "Стоимость";
            default: return column;
        }
    }

    private String format(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
    }

    private List<String> getDefaultColumns() {
        return Arrays.asList("id", "name", "type", "model", "serialNumber", "location",
                "purchaseDate", "warrantyExpiration", "lastMaintenance", "nextMaintenance", "status", "supplier", "cost");
    }
}

