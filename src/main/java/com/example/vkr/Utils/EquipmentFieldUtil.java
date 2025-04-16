package com.example.vkr.Utils;

import com.example.vkr.Models.Equipment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

public class EquipmentFieldUtil {

    public static Object getGroupingValue(Equipment equipment, String fieldName) {
        // реализация получения значения поля для группировки
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
                return equipment.getPurchaseDate() != null ?
                        String.valueOf(equipment.getPurchaseDate().getYear()) :
                        "Не указан";
            default:
                return "Неизвестное поле";
        }
    }

    public static Double getChartValue(Equipment equipment, String valueField) {
        // реализация получения числового значения для графика
        if (valueField == null) return 0.0;

        switch (valueField.toLowerCase()) {
            case "cost":
                return equipment.getCost() != null ? equipment.getCost() : 0.0;
            default:
                return 1.0; // для подсчета количества
        }
    }

    public static String getFieldDisplayName(String field) {
        if (field == null) return "Неизвестное поле";

        return switch (field.toLowerCase()) {
            case "type" -> "Тип оборудования";
            case "location" -> "Локация";
            case "status" -> "Статус";
            case "supplier" -> "Поставщик";
            case "cost" -> "Стоимость";
            case "count" -> "Количество";
            case "purchaseyear" -> "Год покупки";
            default -> field;
        };
    }

    public static String getValueDisplayName(String valueField) {
        return getFieldDisplayName(valueField);
    }
}
