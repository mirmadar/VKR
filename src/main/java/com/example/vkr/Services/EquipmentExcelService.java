package com.example.vkr.Services;

import com.example.vkr.DTO.AnalyticsInfoDTO;
import com.example.vkr.Models.Equipment;
import com.example.vkr.Utils.EquipmentExcelBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class EquipmentExcelService {

    public byte[] export(
            List<Equipment> equipmentList,
            List<String> columns,
            String chartType,
            String groupByField,
            String valueField,
            String subGroupByField
    ) throws IOException {

        ByteArrayInputStream stream = EquipmentExcelBuilder.exportToExcel(
                equipmentList,
                columns,
                chartType,
                groupByField,
                valueField,
                subGroupByField,
                null // ❗ analytics явно null
        );

        return stream.readAllBytes();
    }

    public byte[] export(
            List<Equipment> equipmentList,
            List<String> columns,
            String chartType,
            String groupByField,
            String valueField,
            String subGroupByField,
            List<AnalyticsInfoDTO> analytics
    ) throws IOException {

        ByteArrayInputStream stream = EquipmentExcelBuilder.exportToExcel(
                equipmentList,
                columns,
                chartType,
                groupByField,
                valueField,
                subGroupByField,
                analytics
        );

        return stream.readAllBytes();
    }
}
