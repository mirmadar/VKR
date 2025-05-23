package com.example.vkr.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;

import java.util.Date;

public class ExcelStyleUtil {

    // Стиль для заголовков таблицы
    public static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Стиль для даты (формат dd.MM.yyyy)
    public static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.getCreationHelper()
                .createDataFormat().getFormat("dd.MM.yyyy"));
        return style;
    }

    // Стиль для чисел с двумя знаками после запятой
    public static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat((short) 4); // Формат с 2 знаками после запятой
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    // Метод для установки значения в ячейку с учетом типа данных
    public static void setCellValue(Cell cell, Object value, CellStyle dateStyle, CellStyle numberStyle) {
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

    // Стиль для критичного значения
    public static CellStyle createCriticalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Стиль для предупреждающего значения
    public static CellStyle createWarningStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Стиль для низкого износа (зелёный)
    public static CellStyle createOkStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Желтый (средний износ)
    public static CellStyle createMediumStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Оранжевый (высокий износ)
    public static CellStyle createHighStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }


}