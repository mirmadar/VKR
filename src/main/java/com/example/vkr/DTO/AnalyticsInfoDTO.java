package com.example.vkr.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyticsInfoDTO {
    private Integer equipmentId;
    private String name;

    private Double numericValue;     // Например: 2.5 (года)
    private String formattedComment; // Например: "Гарантия истекла 1.5 года назад"
    private String type;             // Например: "warranty", "maintenance", "wear"
}
