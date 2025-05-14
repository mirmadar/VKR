package com.example.vkr.Utils;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.Models.Equipment;
import org.apache.poi.ss.usermodel.*;

import java.util.Map;
import java.util.function.Function;

public class EquipmentFieldUtil {

    public static Object getGroupingValue(Equipment equipment, String fieldName) {
        if (fieldName == null || equipment == null) return "Неизвестное поле";

        // Поддержка поля "purchaseYear", которого нет в COLUMN_DISPLAY_NAMES, но есть в COLUMN_MAPPERS
        if ("purchaseyear".equalsIgnoreCase(fieldName)) {
            return equipment.getPurchaseDate() != null
                    ? String.valueOf(equipment.getPurchaseDate().getYear())
                    : "Не указан";
        }

        // Используем общий маппер
        Function<Equipment, Object> mapper = EquipmentColumnsConfig.COLUMN_MAPPERS.get(fieldName);
        if (mapper != null) {
            Object value = mapper.apply(equipment);
            return value != null ? value : "Не указан";
        }

        return "Неизвестное поле";
    }

    public static Double getChartValue(Equipment equipment, String valueField) {
        if (valueField == null || equipment == null) return 0.0;

        switch (valueField.toLowerCase()) {
            case "cost":
                return equipment.getCost() != null ? equipment.getCost() : 0.0;
            default:
                return 1.0; // Подсчет количества
        }
    }

    public static String getFieldDisplayName(String field) {
        if (field == null) return "Неизвестное поле";

        // Сначала ищем в стандартных колонках
        if (EquipmentColumnsConfig.COLUMN_DISPLAY_NAMES.containsKey(field)) {
            return EquipmentColumnsConfig.COLUMN_DISPLAY_NAMES.get(field);
        }

        // Потом в аналитических
        if (EquipmentColumnsConfig.ANALYTICS_DISPLAY_NAMES.containsKey(field)) {
            return EquipmentColumnsConfig.ANALYTICS_DISPLAY_NAMES.get(field);
        }

        // Обрабатываем специальные поля
        return switch (field.toLowerCase()) {
            case "purchaseyear" -> "Год покупки";
            case "count" -> "Количество";
            default -> field;
        };
    }

    public static String getValueDisplayName(String valueField) {
        return getFieldDisplayName(valueField)
                ;
    }

    public static String getPrintableValue(Equipment equipment, String fieldName) {
        if (equipment == null || fieldName == null) return "";

        // 1. Сначала проверим в конфиге строковых мапперов
        Function<Equipment, String> mapper = EquipmentColumnsConfig.COLUMN_STRING_MAPPERS.get(fieldName);
        if (mapper != null) {
            return mapper.apply(equipment);
        }

        // 2. Обработка поля, которое есть только в логике, а не в COLUMN_STRING_MAPPERS
        if ("purchaseyear".equalsIgnoreCase(fieldName)) {
            return equipment.getPurchaseDate() != null
                    ? String.valueOf(equipment.getPurchaseDate().getYear())
                    : "Не указан";
        }

        return "";
    }

    // Стиль для критичного значения
    public static CellStyle createCriticalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Стиль для предупреждающего значения
    public static CellStyle createWarningStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

}