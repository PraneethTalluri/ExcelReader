package com.praneeth.excel.reader.service;

import com.praneeth.excel.reader.dto.User;
import com.praneeth.excel.reader.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private ExcelUtils excelUtils;

    static String SHEET = "Sheet1";

//    public List<User> excelToUser(InputStream is) {
//        try {
//            Workbook workbook = new XSSFWorkbook(is);
//
//            Sheet sheet = workbook.getSheet(SHEET);
//            Iterator<Row> rows = sheet.iterator();
//
//            List<User> users = new ArrayList<User>();
//
//            int rowNumber = 0;
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//
//                // skip header
//                if (rowNumber == 0) {
//                    rowNumber++;
//                    continue;
//                }
//
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//
//                User user = new User();
//
//                int cellIdx = 0;
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//
//                    switch (cellIdx) {
//                        case 0:
//                            user.setName(currentCell.getStringCellValue());
//                            break;
//
//                        case 1:
//                            user.setDob(currentCell.getLocalDateTimeCellValue());
//                            break;
//
//                        case 2:
//                            user.setPhoneNumber(currentCell.getNumericCellValue());
//                            break;
//
//                        default:
//                            break;
//                    }
//
//                    cellIdx++;
//                }
//
//                users.add(user);
//            }
//
//            workbook.close();
//
//            return users;
//        } catch (IOException e) {
//            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
//        }
//    }

    public <T> List<T> excelToPojo(MultipartFile multipartFile, Class<T> beanClass, List<String> errors) throws Exception {
        Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<T> list = excelUtils.excelSheetToPOJO(sheet, beanClass, errors);
        return list;
    }

    public <T> Workbook appendPojoToExcel(MultipartFile multipartFile, List<T> bean) throws Exception {
        Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        excelUtils.appendPojoToExcelSheet(sheet, bean);
        return workbook;
    }
}

