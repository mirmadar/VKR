package com.example.vkr.Services;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.Models.Equipment;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                    "static/freesans.ttf", // Убедитесь, что путь к шрифту правильный
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            FONT_HEADER = new Font(baseFont, 10, Font.BOLD, BaseColor.BLACK); // Размер шрифта заголовка в таблице совпадает с размером текста
            FONT_DATA = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK); // Размер шрифта для данных в таблице
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize fonts", e);
        }
    }

    public void export(OutputStream outputStream) throws IOException {
        Document document = new Document(PageSize.A4); // Книжная ориентация
        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Заголовок отчета
            Paragraph title = new Paragraph("Отчет по оборудованию", FONT_HEADER);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f); // Отступ после заголовка
            document.add(title);

            // Добавление текущей даты
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            Paragraph dateParagraph = new Paragraph("Дата: " + currentDate, FONT_DATA);
            dateParagraph.setAlignment(Element.ALIGN_LEFT);
            dateParagraph.setSpacingAfter(10f); // Отступ после даты
            document.add(dateParagraph);

            // Если данных нет
            if (equipmentList.isEmpty()) {
                document.add(new Paragraph("Нет данных для отображения", FONT_DATA));
            } else {
                // Создаем таблицу с данными
                PdfPTable table = new PdfPTable(columns.size());
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                // Заголовки таблицы
                for (String column : columns) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(column, FONT_HEADER));
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Можно задать цвет фона для заголовков
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setPadding(6f);
                    table.addCell(headerCell);
                }

                // Заполнение таблицы данными
                for (Equipment eq : equipmentList) {
                    for (String col : columns) {
                        String value = getValue(eq, col);
                        PdfPCell cell = new PdfPCell(new Phrase(value, FONT_DATA));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPadding(6f);
                        table.addCell(cell);
                    }
                }

                document.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Логируем ошибку на сервере
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

    private String format(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
    }

    private List<String> getDefaultColumns() {
        // Получаем доступные колонки из EquipmentColumnsConfig
        List<String> availableColumns = new ArrayList<>(EquipmentColumnsConfig.COLUMN_MAPPERS.keySet());
        return availableColumns;
    }
}
