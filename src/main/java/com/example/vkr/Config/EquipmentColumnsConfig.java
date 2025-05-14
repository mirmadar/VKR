package com.example.vkr.Config;

import com.example.vkr.Models.Equipment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class EquipmentColumnsConfig {

    public static final Map<String, String> COLUMN_DISPLAY_NAMES = new LinkedHashMap<>() {{
        put("name", "Название");
        put("type", "Тип оборудования");
        put("model", "Модель");
        put("serialNumber", "Серийный номер");
        put("location", "Локация");
        put("purchaseDate", "Дата закупки");
        put("warrantyExpiration", "Гарантия до");
        put("lastMaintenance", "Последнее ТО");
        put("nextMaintenance", "Следующее ТО");
        put("status", "Статус");
        put("supplier", "Поставщик");
        put("cost", "Стоимость");
    }};

    public static final Map<String, String> ANALYTICS_DISPLAY_NAMES = new LinkedHashMap<>() {{
        put("remainingWarrantyYears", "До окончания гарантии (лет)");
        put("remainingWarrantyComment", "Комментарий по гарантии");
        put("remainingMaintenanceYears", "До следующего ТО (лет)");
        put("remainingMaintenanceComment", "Комментарий по ТО");
    }};

    public static final Map<String, Function<Equipment, Object>> COLUMN_MAPPERS = new LinkedHashMap<>() {{
        put("name", Equipment::getName);
        put("type", Equipment::getType);
        put("model", Equipment::getModel);
        put("serialNumber", Equipment::getSerialNumber);
        put("location", Equipment::getLocation);
        put("purchaseDate", Equipment::getPurchaseDate);
        put("warrantyExpiration", Equipment::getWarrantyExpiration);
        put("lastMaintenance", Equipment::getLastMaintenance);
        put("nextMaintenance", Equipment::getNextMaintenance);
        put("status", Equipment::getStatus);
        put("supplier", Equipment::getSupplier);
        put("cost", Equipment::getCost);
        put("purchaseYear", e -> e.getPurchaseDate() != null ? e.getPurchaseDate().getYear() : null);
    }};

    public static final Map<String, Function<Equipment, String>> COLUMN_STRING_MAPPERS = new LinkedHashMap<>() {{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        put("name", e -> safe(e.getName()));
        put("type", e -> safe(e.getType()));
        put("model", e -> safe(e.getModel()));
        put("serialNumber", e -> safe(e.getSerialNumber()));
        put("location", e -> safe(e.getLocation()));
        put("purchaseDate", e -> e.getPurchaseDate() != null ? e.getPurchaseDate().format(formatter) : "");
        put("warrantyExpiration", e -> e.getWarrantyExpiration() != null ? e.getWarrantyExpiration().format(formatter) : "");
        put("lastMaintenance", e -> e.getLastMaintenance() != null ? e.getLastMaintenance().format(formatter) : "");
        put("nextMaintenance", e -> e.getNextMaintenance() != null ? e.getNextMaintenance().format(formatter) : "");
        put("status", e -> safe(e.getStatus()));
        put("supplier", e -> safe(e.getSupplier()));
        put("cost", e -> e.getCost() != null ? String.format(Locale.US, "%.2f", e.getCost()) : "");
    }};

    private static String safe(String s) {
        return s != null ? s : "";
    }
}