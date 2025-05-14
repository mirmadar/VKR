package com.example.vkr.Utils;

import com.example.vkr.DTO.AnalyticsInfoDTO;
import com.example.vkr.Models.Equipment;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class EquipmentAnalyticsUtil {

    public static List<AnalyticsInfoDTO> buildWarrantyAnalytics(List<Equipment> equipmentList) {
        LocalDate today = LocalDate.now();

        return equipmentList.stream()
                .map(eq -> {
                    AnalyticsInfoDTO dto = new AnalyticsInfoDTO();
                    dto.setEquipmentId(eq.getId());
                    dto.setName(eq.getName());
                    dto.setType("warranty");

                    LocalDate warranty = eq.getWarrantyExpiration();
                    if (warranty != null) {
                        long days = ChronoUnit.DAYS.between(today, warranty);
                        double years = Math.abs(days / 365.0);
                        years = Math.round(years * 10.0) / 10.0;

                        dto.setNumericValue(days > 0 ? years : 0.0);
                        dto.setFormattedComment(days >= 0
                                ? "Осталось ≈ " + years + " лет"
                                : "Истекла ≈ " + years + " лет назад");
                    } else {
                        dto.setFormattedComment("Не указана");
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}