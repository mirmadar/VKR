package com.example.vkr.Config;

import com.example.vkr.Models.Equipment;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class EquipmentColumnsConfig {

    public static final List<String> COLUMN_NAMES = Arrays.asList(
            "ID", "Название", "Тип", "Модель", "Серийный номер", "Локация",
            "Дата закупки", "Гарантия до", "Последнее ТО", "Следующее ТО",
            "Статус", "Поставщик", "Стоимость"
    );

    public static final Map<String, Function<Equipment, Object>> COLUMN_MAPPERS = new LinkedHashMap<>();

    static {
        COLUMN_MAPPERS.put("Название", Equipment::getName);
        COLUMN_MAPPERS.put("Тип", Equipment::getType);
        COLUMN_MAPPERS.put("Модель", Equipment::getModel);
        COLUMN_MAPPERS.put("Серийный номер", Equipment::getSerialNumber);
        COLUMN_MAPPERS.put("Локация", Equipment::getLocation);
        COLUMN_MAPPERS.put("Дата закупки", eq -> formatDate(eq.getPurchaseDate()));
        COLUMN_MAPPERS.put("Гарантия до", eq -> formatDate(eq.getWarrantyExpiration()));
        COLUMN_MAPPERS.put("Последнее ТО", eq -> formatDate(eq.getLastMaintenance()));
        COLUMN_MAPPERS.put("Следующее ТО", eq -> formatDate(eq.getNextMaintenance()));
        COLUMN_MAPPERS.put("Статус", Equipment::getStatus);
        COLUMN_MAPPERS.put("Поставщик", Equipment::getSupplier);
        COLUMN_MAPPERS.put("Стоимость", eq -> eq.getCost() != null ? eq.getCost() : 0.0);
    }

    private static String formatDate(LocalDate date) {
        return date != null ? date.toString() : "";
    }
}