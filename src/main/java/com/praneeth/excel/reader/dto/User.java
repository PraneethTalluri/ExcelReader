package com.praneeth.excel.reader.dto;


import com.praneeth.excel.reader.annotation.ExcelColumn;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User {

    @ExcelColumn(name = "rownumber")
    private int rowNumber;

    @ExcelColumn(name = "name")
    @Size(min=0, max=3, message = "Max length of name is 3")
    private String name;

    @ExcelColumn(name = "dob", dateTimeFormat = "yyyy MM dd")
    private Date dob;

    @ExcelColumn(name = "phonenumber") //numberFormat = "$#,##0.00"
    @Digits(integer=8, fraction=0, message = "Max length of phone number is 8")
    private BigDecimal phoneNumber;
}
