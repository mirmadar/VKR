package com.example.vkr.Services;

import com.example.vkr.Config.EquipmentColumnsConfig;
import com.example.vkr.Models.Equipment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EquipmentExcelExporter {

    public static ByteArrayInputStream exportToExcel(
            List<Equipment> equipmentList,
            List<String> columns,
            String chartType,       // "bar", "pie", "line" или null
            String groupByField,    // поле для группировки ("type", "location" и т.д.)
            String valueField      // "cost" или "COUNT"
    ) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 1. Лист с данными оборудования
            Sheet dataSheet = workbook.createSheet("Оборудование");
            fillEquipmentData(workbook, dataSheet, equipmentList, columns);

            // 2. Лист с графиком (если указаны параметры)
            if (chartType != null && groupByField != null && valueField != null) {
                createChartSheet(workbook, equipmentList, chartType, groupByField, valueField);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // Перегруженный метод для экспорта без графика
    public static ByteArrayInputStream exportToExcel(
            List<Equipment> equipmentList,
            List<String> columns
    ) throws IOException {
        return exportToExcel(equipmentList, columns, null, null, null);
    }

    private static void createChartSheet(
            Workbook workbook,
            List<Equipment> equipmentList,
            String chartType,
            String groupByField,
            String valueField
    ) {
        if (!(workbook instanceof XSSFWorkbook)) {
            throw new IllegalArgumentException("Для создания графиков требуется XSSFWorkbook");
        }

        Sheet chartSheet = workbook.createSheet("Аналитика");
        ChartData chartData = generateChartData(equipmentList, groupByField, valueField);
        createChart((XSSFSheet) chartSheet, chartData, chartType);
    }

    private static void fillEquipmentData(
            Workbook workbook,
            Sheet sheet,
            List<Equipment> equipmentList,
            List<String> columns
    ) {
        // Стили для ячеек
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        // Фильтрация и проверка колонок
        List<String> columnsToExport = columns.stream()
                .filter(EquipmentColumnsConfig.COLUMN_MAPPERS::containsKey)
                .collect(Collectors.toList());

        if (columnsToExport.isEmpty()) {
            columnsToExport = new ArrayList<>(EquipmentColumnsConfig.COLUMN_MAPPERS.keySet());
        }

        // Заголовки таблицы
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnsToExport.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(getFieldDisplayName(columnsToExport.get(i)));
            cell.setCellStyle(headerStyle);
        }

        // Заполнение данных
        for (int i = 0; i < equipmentList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Equipment eq = equipmentList.get(i);

            for (int j = 0; j < columnsToExport.size(); j++) {
                String columnName = columnsToExport.get(j);
                Object value = EquipmentColumnsConfig.COLUMN_MAPPERS.get(columnName).apply(eq);
                setCellValue(row.createCell(j), value, dateStyle, numberStyle);
            }
        }

        // Автоподбор ширины колонок
        for (int i = 0; i < columnsToExport.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static ChartData generateChartData(
            List<Equipment> equipmentList,
            String groupByField,
            String valueField
    ) {
        ChartData chartData = new ChartData();

        // Группировка данных
        Map<String, Double> groupedData = equipmentList.stream()
                .collect(Collectors.groupingBy(
                        e -> getGroupingValue(e, groupByField),
                        Collectors.summingDouble(e -> getChartValue(e, valueField))
                ));

        // Сортировка по значению
        List<Map.Entry<String, Double>> sortedEntries = groupedData.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // Заполнение данных для графика
        chartData.setLabels(sortedEntries.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        chartData.setValues(sortedEntries.stream().map(Map.Entry::getValue).collect(Collectors.toList()));
        chartData.setTitle("Анализ по " + getFieldDisplayName(groupByField));
        chartData.setDatasetLabel(getValueDisplayName(valueField));

        return chartData;
    }

    private static void createChart(
            XSSFSheet sheet,
            ChartData chartData,
            String chartType
    ) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 3, 15, 25);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(chartData.getTitle());

        // Создание графика в зависимости от типа
        XDDFChartData data;
        switch (chartType.toLowerCase()) {
            case "pie":
                data = chart.createData(ChartTypes.PIE, null, null);
                break;
            case "bar":
                XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
                XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
                leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
                data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
                ((XDDFBarChartData) data).setBarDirection(BarDirection.COL);
                break;
            case "line":
                XDDFCategoryAxis catAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
                XDDFValueAxis valAxis = chart.createValueAxis(AxisPosition.LEFT);
                data = chart.createData(ChartTypes.LINE, catAxis, valAxis);
                break;
            default:
                throw new IllegalArgumentException("Неподдерживаемый тип графика: " + chartType);
        }

        // Добавление данных в график
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromArray(
                chartData.getLabels().toArray(new String[0]));
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromArray(
                chartData.getValues().toArray(new Double[0]));

        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle(chartData.getDatasetLabel(), null);
        chart.plot(data);
    }

    // Вспомогательные методы
    private static String getGroupingValue(Equipment e, String field) {
        if (field == null) return "";
        switch (field.toLowerCase()) {
            case "type": return e.getType() != null ? e.getType() : "Не указано";
            case "location": return e.getLocation() != null ? e.getLocation() : "Не указано";
            case "status": return e.getStatus() != null ? e.getStatus() : "Не указано";
            case "supplier": return e.getSupplier() != null ? e.getSupplier() : "Не указано";
            case "purchaseyear":
                return e.getPurchaseDate() != null ?
                        String.valueOf(e.getPurchaseDate().getYear()) : "Не указано";
            default: return field;
        }
    }

    private static double getChartValue(Equipment e, String field) {
        if (field == null) return 1;
        return "cost".equalsIgnoreCase(field) ?
                (e.getCost() != null ? e.getCost() : 0) : 1;
    }

    private static String getFieldDisplayName(String field) {
        if (field == null) return "";
        switch (field.toLowerCase()) {
            case "type": return "Тип оборудования";
            case "location": return "Локация";
            case "status": return "Статус";
            case "supplier": return "Поставщик";
            case "purchaseyear": return "Год покупки";
            case "cost": return "Стоимость";
            case "name": return "Название";
            case "model": return "Модель";
            case "serialnumber": return "Серийный номер";
            case "purchasedate": return "Дата покупки";
            case "warrantyexpiration": return "Гарантия до";
            case "lastmaintenance": return "Последнее ТО";
            case "nextmaintenance": return "Следующее ТО";
            default: return field;
        }
    }

    private static String getValueDisplayName(String field) {
        if (field == null) return "Количество";
        return "cost".equalsIgnoreCase(field) ? "Стоимость" : "Количество";
    }

    private static void setCellValue(Cell cell, Object value, CellStyle dateStyle, CellStyle numberStyle) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            cell.setCellStyle(dateStyle);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
            cell.setCellStyle(numberStyle);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.getCreationHelper()
                .createDataFormat().getFormat("dd.MM.yyyy"));
        return style;
    }

    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat((short) 4); // Формат с 2 десятичными знаками
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private static class ChartData {
        private List<String> labels;
        private List<Double> values;
        private String title;
        private String datasetLabel;

        // Геттеры и сеттеры
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        public List<Double> getValues() { return values; }
        public void setValues(List<Double> values) { this.values = values; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDatasetLabel() { return datasetLabel; }
        public void setDatasetLabel(String datasetLabel) { this.datasetLabel = datasetLabel; }
    }
}