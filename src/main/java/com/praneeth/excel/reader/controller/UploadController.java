package com.praneeth.excel.reader.controller;

import com.praneeth.excel.reader.dto.ResponseMessage;
import com.praneeth.excel.reader.dto.User;
import com.praneeth.excel.reader.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> updateDriverPayDetails(@RequestParam("file") MultipartFile excelFile) {
        String message;

        String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        if (!TYPE.equals(excelFile.getContentType())) {
            try {
                List<User> users = uploadService.excelToUser(excelFile.getInputStream());

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

}