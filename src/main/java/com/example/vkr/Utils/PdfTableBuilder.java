package com.example.vkr.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PdfTableBuilder<T> {

    private final List<T> data;
    private final List<String> columns;
    private final Map<String, String> columnHeaders;
    private final Map<String, Function<T, String>> columnMappers;

    public PdfTableBuilder(List<T> data, List<String> columns,
                           Map<String, String> columnHeaders,
                           Map<String, Function<T, String>> columnMappers) {
        this.data = data;
        this.columns = columns;
        this.columnHeaders = columnHeaders;
        this.columnMappers = columnMappers;
    }

    public void build(OutputStream outputStream, String title) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Загрузка шрифта из ресурсов
        BaseFont baseFont = BaseFont.createFont(
                getClass().getClassLoader().getResource("static/freesans.ttf").toString(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );
        Font headerFont = new Font(baseFont, 12, Font.BOLD);
        Font dataFont = new Font(baseFont, 12);

        // Заголовок
        Paragraph titleParagraph = new Paragraph(title, headerFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);
        document.add(Chunk.NEWLINE);

        // Таблица
        PdfPTable table = new PdfPTable(columns.size());
        table.setWidthPercentage(100);

        // Заголовки колонок
        for (String col : columns) {
            String header = columnHeaders.getOrDefault(col, col);
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Данные
        for (T item : data) {
            for (String col : columns) {
                Function<T, String> mapper = columnMappers.get(col);
                String value = mapper != null ? mapper.apply(item) : "";
                PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
    }
}
