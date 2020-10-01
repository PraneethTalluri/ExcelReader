package com.praneeth.excel.reader.utils;

import com.praneeth.excel.reader.annotation.ExcelColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import java.util.*;

@Component
public class ExcelUtils {

    public <T> List<T> excelSheetToPOJO(Sheet sheet, Class<T> beanClass, List<String> errors) throws Exception {

        DataFormatter formatter = new DataFormatter(java.util.Locale.US);
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        int headerRowNum = sheet.getFirstRowNum();

        // collecting the column headers as a Map of header names to column indexes
        Map<Integer, String> colHeaders = new HashMap<Integer, String>();
        Row row = sheet.getRow(headerRowNum);
        for (Cell cell : row) {
            int colIdx = cell.getColumnIndex();
            String value = formatter.formatCellValue(cell, evaluator);
            colHeaders.put(colIdx, value.replaceAll("\\s", ""));
        }

        // collecting the content rows
        List<T> result = new ArrayList<T>();
        String cellValue = "";
        LocalDateTime localDateTime = null;
        Double num = null;
        for (int r = headerRowNum + 1; r <= sheet.getLastRowNum(); r++) {
            row = sheet.getRow(r);
            if (row == null) row = sheet.createRow(r);
            T bean = beanClass.getDeclaredConstructor().newInstance();

            for (Map.Entry<Integer, String> entry : colHeaders.entrySet()) {
                int colIdx = entry.getKey();
                Cell cell = row.getCell(colIdx);
                if (cell == null) cell = row.createCell(colIdx);
                cellValue = formatter.formatCellValue(cell, evaluator); // string values and formatted numbers
                // make some differences for numeric or formula content
                localDateTime = null;
                num = null;
                if (cell.getCellType() == CellType.NUMERIC) {
//                    if (DateUtil.isCellDateFormatted(cell)) { // date
//                        localDateTime = cell.getLocalDateTimeCellValue();
//                    } else { // other numbers
                    num = cell.getNumericCellValue();
//                    }
                } else if (cell.getCellType() == CellType.FORMULA) {
                    // if formula evaluates to numeric
                    if (evaluator.evaluateFormulaCell(cell) == CellType.NUMERIC) {
//                        if (DateUtil.isCellDateFormatted(cell)) { // date
//                            localDateTime = cell.getLocalDateTimeCellValue();
//                        } else { // other numbers
                        num = cell.getNumericCellValue();
//                        }
                    }
                }

                // fill the bean
                for (Field f : beanClass.getDeclaredFields()) {
                    if (!f.isAnnotationPresent(ExcelColumn.class)) {
                        continue;
                    }
                    ExcelColumn ec = f.getAnnotation(ExcelColumn.class);
                    String dateTimeFormat = ec.dateTimeFormat();
//                    if (entry.getValue().equals(ec.name())) {
                    if (entry.getValue().contains(ec.name())) {
                        f.setAccessible(true);
                        try {
                            if (f.getType() == String.class) {
                                f.set(bean, cellValue);
                            } else if (f.getType() == Double.class && num != null) {
                                f.set(bean, num);
//                            } else if (f.getType() == LocalDateTime.class) {
//                                if (localDateTime != null)
//                                    f.set(bean, localDateTime);
//                                else if (!"".equals(cellValue)) {
//                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
//                                    TemporalAccessor parsed = dateTimeFormatter.parseBest(cellValue, LocalDateTime::from, LocalDate::from);
//                                    LocalDateTime formattedDateTime = null;
//                                    if (parsed instanceof LocalDateTime) {
//                                        // it's a LocalDateTime, just assign it
//                                        formattedDateTime = (LocalDateTime) parsed;
//                                    } else if (parsed instanceof LocalDate) {
//                                        // it's a LocalDate,
//                                        formattedDateTime = ((LocalDate) parsed).atTime(LocalTime.MIDNIGHT);
//                                    }
//                                    f.set(bean, formattedDateTime);
//                                }
                            } else if (f.getType() == Date.class) {
//                                Instant instant = null;
//                                if (localDateTime != null) {
//                                    instant = localDateTime.toInstant(ZoneOffset.UTC);
//                                } else if (!"".equals(cellValue)) {
//                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
//                                    TemporalAccessor parsed = dateTimeFormatter.parseBest(cellValue, LocalDateTime::from, LocalDate::from);
//                                    LocalDateTime formattedDateTime = null;
//                                    if (parsed instanceof LocalDateTime) {
//                                        // it's a LocalDateTime, just assign it
//                                        formattedDateTime = (LocalDateTime) parsed;
//                                    } else if (parsed instanceof LocalDate) {
//                                        // it's a LocalDate,
//                                        formattedDateTime = ((LocalDate) parsed).atTime(LocalTime.MIDNIGHT);
//                                    }
//                                    instant = formattedDateTime.toInstant(ZoneOffset.UTC);
//                                }
                                if (!"".equals(cellValue)) {
                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
                                    LocalDate localDate = LocalDate.parse(cellValue, dateTimeFormatter);
                                    Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
                                    f.set(bean, Date.from(instant));
                                }
                            } else if (f.getType() == BigDecimal.class && num != null) {
                                f.set(bean, new BigDecimal(num));
                            } else { // this is for all other; Integer, Boolean, ...
                                if (!"".equals(cellValue)) {
                                    Method valueOf = f.getType().getDeclaredMethod("valueOf", String.class);
                                    f.set(bean, valueOf.invoke(f.getType(), cellValue));
                                }
                            }
                        } catch (Exception e) {
                            errors.add("Failed to convert " + cellValue + " at row number: " + r + " and column number: " + colIdx);
                        }
                    }
                }
            }
            result.add(bean);
        }
        return result;
    }

    public <T> void pojoToExcelSheet(Sheet sheet, List<T> rows) throws Exception {
        if (rows.size() > 0) {
            Row row = null;
            Cell cell = null;
            int r = 0;
            int c = 0;
            int colCount = 0;
            Map<String, Object> properties = null;
            DataFormat dataFormat = sheet.getWorkbook().createDataFormat();

            Class beanClass = rows.get(0).getClass();

            // header row
            row = sheet.createRow(r++);
            for (Field f : beanClass.getDeclaredFields()) {
                if (!f.isAnnotationPresent(ExcelColumn.class)) {
                    continue;
                }
                ExcelColumn ec = f.getAnnotation(ExcelColumn.class);
                cell = row.createCell(c++);
                // do formatting the header row
                properties = new HashMap<String, Object>();
                properties.put(CellUtil.FILL_PATTERN, FillPatternType.SOLID_FOREGROUND);
                properties.put(CellUtil.FILL_FOREGROUND_COLOR, IndexedColors.GREY_25_PERCENT.getIndex());
                CellUtil.setCellStyleProperties(cell, properties);
                cell.setCellValue(ec.name());
            }

            colCount = c;

            // contents
            for (T bean : rows) {
                c = 0;
                row = sheet.createRow(r++);
                for (Field f : beanClass.getDeclaredFields()) {
                    cell = row.createCell(c++);
                    if (!f.isAnnotationPresent(ExcelColumn.class)) {
                        continue;
                    }
                    ExcelColumn ec = f.getAnnotation(ExcelColumn.class);
                    // do number formatting the contents
                    String numberFormat = ec.numberFormat();
                    properties = new HashMap<String, Object>();
                    properties.put(CellUtil.DATA_FORMAT, dataFormat.getFormat(numberFormat));
                    CellUtil.setCellStyleProperties(cell, properties);

                    f.setAccessible(true);
                    Object value = f.get(bean);
                    if (value != null) {
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof LocalDateTime) {
                            cell.setCellValue((LocalDateTime) value);
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        }
                    }
                }
            }

            // auto size columns
            for (int col = 0; col < colCount; col++) {
                sheet.autoSizeColumn(col);
            }
        }
    }
}
