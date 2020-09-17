package com.praneeth.excel.reader.dto;


import com.praneeth.excel.reader.annotation.ExcelColumn;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    @ExcelColumn(name = "name")
    private String name;

    @ExcelColumn(name = "dob")
    private LocalDateTime dob;

    @ExcelColumn(name = "phone number", numberFormat = "0")
    private Integer phoneNumber;
}
