package com.example.vkr.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;

import java.util.*;
import java.util.stream.Collectors;

public class ExcelChartBuilder {

    public static void createStackedBarChart(
            XSSFSheet sheet,
            int chartRow,
            int chartCol,
            Map<String, Map<String, Double>> groupedData,
            String chartTitle,
            String xAxisLabel,
            String yAxisLabel
    ) {
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, chartCol, chartRow, chartCol + 15, chartRow + 25);
        anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

        XSSFChart chart = ((XSSFDrawing) drawing).createChart(anchor);
        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);

        // Осевые линии
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(xAxisLabel);
        bottomAxis.setMajorTickMark(AxisTickMark.OUT);
        bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle(yAxisLabel);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // Категории по оси X
        List<String> mainCategories = new ArrayList<>(groupedData.keySet());
        Set<String> subCategories = groupedData.values().stream()
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        String[] categoryArray = mainCategories.toArray(new String[0]);

        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categoryArray);

        XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        data.setBarGrouping(BarGrouping.STACKED);
        data.setBarDirection(BarDirection.COL);

        // Устанавливаем плотность и ширину
        data.setVaryColors(false);
        data.setOverlap((byte) 100); // Столбцы плотно прилегают
        data.setGapWidth(150); // Расстояние между группами

        int colorIndex = 0;
        for (String subCat : subCategories) {
            Double[] values = mainCategories.stream()
                    .map(cat -> groupedData.get(cat).getOrDefault(subCat, 0.0))
                    .toArray(Double[]::new);

            XDDFNumericalDataSource<Double> valuesData = XDDFDataSourcesFactory.fromArray(values);

            XDDFDataSource<String> subCategoryData = XDDFDataSourcesFactory.fromArray(categoryArray);
            XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(subCategoryData, valuesData);
            series.setTitle(subCat, null);

            // Цвет
            XDDFColor color = XDDFColor.from(getRandomColorBytes(colorIndex++));
            XDDFSolidFillProperties fill = new XDDFSolidFillProperties(color);
            XDDFShapeProperties properties = new XDDFShapeProperties();
            properties.setFillProperties(fill);
            series.setShapeProperties(properties);
        }

        chart.plot(data);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.RIGHT);
    }


    private static byte[] getRandomColorBytes(int index) {
        // Пример фиксированных, но "разных" цветов для серий
        int[][] palette = {
                {255, 99, 132}, {54, 162, 235}, {255, 206, 86},
                {75, 192, 192}, {153, 102, 255}, {255, 159, 64}
        };
        int[] rgb = palette[index % palette.length];
        return new byte[]{(byte) rgb[0], (byte) rgb[1], (byte) rgb[2]};
    }

    public static void createBarChart(
            XSSFSheet sheet,
            int chartRow,
            int chartCol,
            Map<String, Double> data,
            String chartTitle,
            String xAxisLabel,
            String yAxisLabel
    ) {
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, chartCol, chartRow, chartCol + 10, chartRow + 20);
        anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

        XSSFChart chart = ((XSSFDrawing) drawing).createChart(anchor);
        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);

        // Создание осей
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(xAxisLabel);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle(yAxisLabel);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // Категории (ось X)
        List<String> categories = new ArrayList<>(data.keySet());
        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categories.toArray(new String[0]));

        // Значения (ось Y)
        List<Double> values = new ArrayList<>(data.values());
        XDDFNumericalDataSource<Double> valuesData = XDDFDataSourcesFactory.fromArray(values.toArray(new Double[0]));

        // Создание bar chart
        XDDFChartData chartData = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        ((XDDFBarChartData) chartData).setBarDirection(BarDirection.COL);

        XDDFChartData.Series series = chartData.addSeries(categoryData, valuesData);
        series.setTitle(yAxisLabel, null);

        chart.plot(chartData);
    }

    public static void createPieChart(
            XSSFSheet sheet,
            int row, int col,
            Map<String, Double> data,
            String title
    ) {
        // Создаем объект диаграммы
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, col, row, col + 10, row + 20);
        anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(title);
        chart.setTitleOverlay(false);

        // Создаем категории (ось X)
        List<String> categories = new ArrayList<>(data.keySet());
        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categories.toArray(new String[0]));

        // Значения (ось Y)
        List<Double> values = new ArrayList<>(data.values());
        XDDFNumericalDataSource<Double> valuesData = XDDFDataSourcesFactory.fromArray(values.toArray(new Double[0]));

        // Устанавливаем данные для круговой диаграммы
        XDDFChartData chartData = chart.createData(ChartTypes.PIE, null, null);
        XDDFChartData.Series series = chartData.addSeries(categoryData, valuesData);
        series.setTitle(title, null);

        chart.plot(chartData);
    }

    public static void createLineChart(
            XSSFSheet sheet,
            int row, int col,
            Map<String, Double> data,
            String title,
            String categoryAxisLabel,
            String valueAxisLabel
    ) {
        // Создаем объект диаграммы
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, col, row, col + 10, row + 20);
        anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(title);
        chart.setTitleOverlay(false);

        // Создаем оси
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(categoryAxisLabel);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle(valueAxisLabel);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // Категории (ось X)
        List<String> categories = new ArrayList<>(data.keySet());
        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categories.toArray(new String[0]));

        // Значения (ось Y)
        List<Double> values = new ArrayList<>(data.values());
        XDDFNumericalDataSource<Double> valuesData = XDDFDataSourcesFactory.fromArray(values.toArray(new Double[0]));

        // Создаем линейный график
        XDDFChartData chartData = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFChartData.Series series = chartData.addSeries(categoryData, valuesData);
        series.setTitle(title, null);

        chart.plot(chartData);
    }

    public static void createLineChartWithGroups(
            XSSFSheet sheet,
            int chartRow,
            int chartCol,
            Map<String, Map<String, Double>> groupedData,
            String chartTitle,
            String xAxisLabel,
            String yAxisLabel
    ) {
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, chartCol, chartRow, chartCol + 15, chartRow + 25);
        anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

        XSSFChart chart = ((XSSFDrawing) drawing).createChart(anchor);
        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(xAxisLabel);

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle(yAxisLabel);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        List<String> categories = new ArrayList<>(groupedData.keySet());
        Set<String> subCategories = groupedData.values().stream()
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categories.toArray(new String[0]));

        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

        int colorIndex = 0;
        for (String subCat : subCategories) {
            Double[] values = categories.stream()
                    .map(cat -> groupedData.get(cat).getOrDefault(subCat, 0.0))
                    .toArray(Double[]::new);

            XDDFNumericalDataSource<Double> valuesData = XDDFDataSourcesFactory.fromArray(values);

            XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(categoryData, valuesData);
            series.setTitle(subCat, null);
            series.setSmooth(false);
            series.setMarkerStyle(MarkerStyle.CIRCLE);

            // Цвет
            XDDFColor color = XDDFColor.from(getRandomColorBytes(colorIndex++));
            XDDFSolidFillProperties fill = new XDDFSolidFillProperties(color);
            XDDFShapeProperties properties = new XDDFShapeProperties();
            properties.setFillProperties(fill);
            series.setShapeProperties(properties);
        }

        chart.plot(data);
        chart.getOrAddLegend().setPosition(LegendPosition.RIGHT);
    }

}
