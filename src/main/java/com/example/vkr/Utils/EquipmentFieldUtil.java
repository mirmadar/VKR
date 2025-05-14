package com.example.vkr.Utils;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.Models.Equipment;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class EquipmentFieldUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static Object getGroupingValue(Equipment equipment, String fieldName) {
        if (fieldName == null || equipment == null) return "Неизвестное поле";

        switch (fieldName.toLowerCase()) {
            case "type":
                return equipment.getType();
            case "location":
                return equipment.getLocation();
            case "status":
                return equipment.getStatus();
            case "supplier":
                return equipment.getSupplier();
            case "purchaseyear":
                return equipment.getPurchaseDate() != null
                        ? String.valueOf(equipment.getPurchaseDate().getYear())
                        : "Не указан";
            default:
                return "Неизвестное поле";
        }
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

        // Пробуем взять из глобального конфига
        Map<String, String> displayNames = EquipmentColumnsConfig.COLUMN_DISPLAY_NAMES;
        return displayNames.getOrDefault(field, switch (field.toLowerCase()) {
            case "purchaseyear" -> "Год покупки";
            case "count" -> "Количество";
            default -> field; // Если не найдено, вернуть как есть
        });
    }

    public static String getValueDisplayName(String valueField) {
        return getFieldDisplayName(valueField);
    }

    public static String getPrintableValue(Equipment equipment, String fieldName) {
        if (equipment == null || fieldName == null) return "";

        return switch (fieldName.toLowerCase()) {
            case "name" -> safeString(equipment.getName());
            case "type" -> safeString(equipment.getType());
            case "model" -> safeString(equipment.getModel());
            case "serialnumber" -> safeString(equipment.getSerialNumber());
            case "location" -> safeString(equipment.getLocation());
            case "status" -> safeString(equipment.getStatus());
            case "supplier" -> safeString(equipment.getSupplier());
            case "cost" -> equipment.getCost() != null
                    ? String.format(Locale.US, "%.2f", equipment.getCost())
                    : "";
            case "purchaseyear" -> equipment.getPurchaseDate() != null
                    ? String.valueOf(equipment.getPurchaseDate().getYear())
                    : "Не указан";
            case "purchasedate" -> formatDate(equipment.getPurchaseDate());
            case "warrantyexpiration" -> formatDate(equipment.getWarrantyExpiration());
            case "lastmaintenance" -> formatDate(equipment.getLastMaintenance());
            case "nextmaintenance" -> formatDate(equipment.getNextMaintenance());
            default -> "";
        };
    }

    private static String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    private static String safeString(String value) {
        return value != null ? value : "";
    }
}
