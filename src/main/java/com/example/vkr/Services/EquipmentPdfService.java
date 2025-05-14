package com.example.vkr.Services;

import com.example.vkr.Models.Equipment;
import com.example.vkr.Utils.EquipmentPdfBuilder;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@Service
public class EquipmentPdfService {

    public void export(List<Equipment> equipmentList, List<String> columns, OutputStream outputStream) {
        EquipmentPdfBuilder exporter = new EquipmentPdfBuilder(equipmentList, columns);
        try {
            exporter.export(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка экспорта PDF: " + e.getMessage(), e);
        }
    }
}
