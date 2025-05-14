package com.example.vkr.Utils;

import com.example.vkr.Models.Equipment;
import com.example.vkr.Config.EquipmentColumnsConfig;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EquipmentPdfBuilder {

    private final List<Equipment> equipmentList;
    private final List<String> columns;

    public EquipmentPdfBuilder(List<Equipment> equipmentList, List<String> columns) {
        this.equipmentList = equipmentList;
        this.columns = columns;
    }

    public void export(OutputStream outputStream) throws Exception {
        Map<String, String> allHeaders = EquipmentColumnsConfig.COLUMN_DISPLAY_NAMES;
        Map<String, Function<Equipment, String>> allMappers = EquipmentColumnsConfig.COLUMN_STRING_MAPPERS;

        // Фильтруем только корректные колонки
        List<String> safeColumns = columns.stream()
                .filter(allMappers::containsKey) // ← только те, у которых есть мапперы
                .toList();

        // Только нужные заголовки (если нет — подставляем ключ)
        Map<String, String> filteredHeaders = safeColumns.stream()
                .collect(Collectors.toMap(
                        col -> col,
                        col -> allHeaders.getOrDefault(col, col),
                        (a, b) -> a
                ));

        // Только нужные мапперы
        Map<String, Function<Equipment, String>> filteredMappers = safeColumns.stream()
                .collect(Collectors.toMap(
                        col -> col,
                        col -> allMappers.getOrDefault(col, eq -> ""),
                        (a, b) -> a
                ));

        PdfTableBuilder<Equipment> builder = new PdfTableBuilder<>(
                equipmentList,
                safeColumns, // ← заменено
                filteredHeaders,
                filteredMappers
        );

        builder.build(outputStream, "Отчет по оборудованию");
    }
}
