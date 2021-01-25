package com.praneeth.excel.reader.controller;

import com.praneeth.excel.reader.dto.ResponseMessage;
import com.praneeth.excel.reader.dto.User;
import com.praneeth.excel.reader.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
public class UploadController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/readData")
    public ResponseEntity<ResponseMessage> readExcelFile(@RequestParam("file") MultipartFile excelFile) {
        String message;

        String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        if (!TYPE.equals(excelFile.getContentType())) {
            try {
                List<String> errors = new ArrayList<>();
                List<User> users = excelService.excelToPojo(excelFile, User.class, errors);
                log.error("Errors:" + errors.toString());

                message = "Uploaded the file successfully: " + excelFile.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message, users));
            } catch (Exception e) {
                message = "Could not upload the file: " + excelFile.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @PostMapping("/appendData")
    public ResponseEntity<byte[]> appendDataToExcelFile(@RequestParam("file") MultipartFile excelFile) {
        String message;

        String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        List<User> users = new ArrayList<>();
        users.add(new User(5, "katy", new Date(12/12/2000), new BigDecimal(4565)));
        users.add(new User(6, "lady", new Date(12/12/1994), new BigDecimal(8965)));

        if (!TYPE.equals(excelFile.getContentType())) {
            try {
                byte[] response = null;
                final HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel;charset=UTF-8");
                httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+excelFile.getOriginalFilename());

                Workbook workbook = excelService.appendPojoToExcel(excelFile, users);
                ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
                workbook.write(outByteStream);
                workbook.close();
                byte [] outArray = outByteStream.toByteArray();
                message = "Downloading the file : " + excelFile.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(outArray);
            } catch (Exception e) {
                message = "Could not upload the file: " + excelFile.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message.getBytes(StandardCharsets.UTF_8));
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message.getBytes(StandardCharsets.UTF_8));
    }

}
