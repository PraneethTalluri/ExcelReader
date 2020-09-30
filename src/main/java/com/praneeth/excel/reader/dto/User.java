package com.praneeth.excel.reader.dto;


import com.praneeth.excel.reader.annotation.ExcelColumn;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User {

    @ExcelColumn(name = "name")
    private String name;

    @ExcelColumn(name = "dob", dateTimeFormat = "yyyy MM dd")
    private LocalDateTime dob;

    @ExcelColumn(name = "phonenumber", numberFormat = "0")
    private Integer phoneNumber;
}
