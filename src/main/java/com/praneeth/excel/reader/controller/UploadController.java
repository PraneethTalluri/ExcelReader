package com.praneeth.excel.reader.controller;

import com.praneeth.excel.reader.dto.ResponseMessage;
import com.praneeth.excel.reader.dto.User;
import com.praneeth.excel.reader.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
                List<String> errors = new ArrayList<>();
                List<User> users = uploadService.excelToPojo(excelFile, User.class, errors);
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

}
