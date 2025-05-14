package com.example.vkr.Services;

import com.example.vkr.Models.Equipment;
import com.example.vkr.Utils.ChartDataUtil;
import com.example.vkr.Utils.EquipmentFieldUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartService {

    public Map<String, Object> generateSimpleChartData(List<Equipment> equipment, String groupByField, String valueField) {
        Map<String, Double> groupedData = ChartDataUtil.generateSimpleChartData(equipment, groupByField, valueField);

        List<Map.Entry<String, Double>> sortedEntries = groupedData.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", sortedEntries.stream().map(Map.Entry::getKey).toList());
        result.put("values", sortedEntries.stream().map(Map.Entry::getValue).toList());
        result.put("title", String.format("%s по %s", EquipmentFieldUtil.getFieldDisplayName(valueField), EquipmentFieldUtil.getFieldDisplayName(groupByField)));
        result.put("datasetLabel", EquipmentFieldUtil.getFieldDisplayName(valueField));
        return result;
    }

    public Map<String, Object> generateGroupedStackedChartData(
            List<Equipment> equipment,
            String groupByField,
            String subGroupByField,
            String valueField
    ) {
        Map<String, Map<String, Double>> grouped = ChartDataUtil.generateStackedChartData(
                equipment, groupByField, subGroupByField, valueField);

        // Ось X (группы, напр. 2020, 2021)
        List<String> labels = new ArrayList<>(grouped.keySet());

        // Все подгруппы (напр. поставщики)
        Set<String> allSubGroups = grouped.values().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Сборка datasets
        List<Map<String, Object>> datasets = new ArrayList<>();
        for (String subGroup : allSubGroups) {
            List<Double> values = labels.stream()
                    .map(group -> grouped.getOrDefault(group, Collections.emptyMap()).getOrDefault(subGroup, 0.0))
                    .collect(Collectors.toList());

            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", subGroup);
            dataset.put("data", values);
            dataset.put("backgroundColor", getRandomColor());

            datasets.add(dataset);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", labels);
        result.put("datasets", datasets);
        result.put("title", String.format("%s по %s и %s",
                EquipmentFieldUtil.getFieldDisplayName(valueField),
                EquipmentFieldUtil.getFieldDisplayName(groupByField),
                EquipmentFieldUtil.getFieldDisplayName(subGroupByField))
        );
        return result;
    }

    private String getRandomColor() {
        String[] colors = {
                "#F3CAE2", "#A0D8EF", "#B0E57C", "#FFD580",
                "#CBAACB", "#FFB7B2", "#FFDAC1", "#B5EAD7",
                "#E2F0CB", "#FF9AA2", "#FFB347", "#77DD77"
        };
        return colors[new Random().nextInt(colors.length)];
    }
}