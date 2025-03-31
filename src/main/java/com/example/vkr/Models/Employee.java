package com.example.vkr.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String position;
    private String department;
    private LocalDate hireDate;
    private double salary;

    private double workload;
    private double overtimeHours;
    private int projectsCount;

    private String vacationStatus;
    private int vacationDaysLeft;
    private int sickLeaveDays;
}

