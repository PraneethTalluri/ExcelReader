package com.praneeth.excel.reader.dto;


import com.praneeth.excel.reader.annotation.ExcelColumn;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User {

    @ExcelColumn(name = "rownumber")
    private int rowNumber;

    @ExcelColumn(name = "name")
    private String name;

    @ExcelColumn(name = "dob", dateTimeFormat = "yyyy MM dd")
    private Date dob;

    @ExcelColumn(name = "phonenumber") //numberFormat = "$#,##0.00"
    private BigDecimal phoneNumber;
}
