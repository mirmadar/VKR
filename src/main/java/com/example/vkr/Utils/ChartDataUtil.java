package com.example.vkr.Utils;

import com.example.vkr.Models.Equipment;

import java.util.*;
import java.util.stream.Collectors;

public class ChartDataUtil {

    public static Map<String, Double> generateSimpleChartData(List<Equipment> equipmentList, String groupByField, String valueField) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Equipment equipment : equipmentList) {
            String groupKey = String.valueOf(EquipmentFieldUtil.getGroupingValue(equipment, groupByField));
            Double value = EquipmentFieldUtil.getChartValue(equipment, valueField);
            result.merge(groupKey, value, Double::sum);
        }
        return result;
    }

    public static Map<String, Map<String, Double>> generateStackedChartData(List<Equipment> equipmentList, String groupByField, String subGroupByField, String valueField) {
        Map<String, Map<String, Double>> result = new LinkedHashMap<>();
        Set<String> allSubCategories = equipmentList.stream()
                .map(e -> String.valueOf(EquipmentFieldUtil.getGroupingValue(e, subGroupByField)))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        equipmentList.stream()
                .map(e -> String.valueOf(EquipmentFieldUtil.getGroupingValue(e, groupByField)))
                .distinct()
                .forEach(mainCat -> {
                    Map<String, Double> subMap = new LinkedHashMap<>();
                    allSubCategories.forEach(subCat -> subMap.put(subCat, 0.0));
                    result.put(mainCat, subMap);
                });

        for (Equipment equipment : equipmentList) {
            String mainCat = String.valueOf(EquipmentFieldUtil.getGroupingValue(equipment, groupByField));
            String subCat = String.valueOf(EquipmentFieldUtil.getGroupingValue(equipment, subGroupByField));
            Double value = EquipmentFieldUtil.getChartValue(equipment, valueField);
            result.get(mainCat).merge(subCat, value, Double::sum);
        }

        return result;
    }
}
