package com.example.vkr.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class EquipmentFilterDTO {
    private String name;
    private String type;
    private String model;
    private String serialNumber;
    private String location;
    private String status;
    private String supplier;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate purchaseDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate purchaseDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate warrantyExpirationFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate warrantyExpirationTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastMaintenanceFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastMaintenanceTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate nextMaintenanceFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate nextMaintenanceTo;

    private Double costMin;
    private Double costMax;
}
