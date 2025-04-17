package com.example.vkr.Utils;

import com.example.vkr.Models.Equipment;

public class EquipmentFieldUtil {

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
                return equipment.getPurchaseDate() != null ? String.valueOf(equipment.getPurchaseDate().getYear()) : "Не указан";
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
                return 1.0; // Для подсчета количества
        }
    }

    public static String getFieldDisplayName(String field) {
        if (field == null) return "Неизвестное поле";

        switch (field.toLowerCase()) {
            case "type":
                return "Тип оборудования";
            case "location":
                return "Локация";
            case "status":
                return "Статус";
            case "supplier":
                return "Поставщик";
            case "cost":
                return "Стоимость";
            case "count":
                return "Количество";
            case "purchaseyear":
                return "Год покупки";
            default:
                return field; // Возвращаем исходное имя, если оно не найдено
        }
    }

    public static String getValueDisplayName(String valueField) {
        return getFieldDisplayName(valueField);
    }
}
