package com.example.vkr.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "equipment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;
    private String model;
    private String serialNumber;
    private String location;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiration;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private String status;
    private String supplier;
    private Double cost;
}

