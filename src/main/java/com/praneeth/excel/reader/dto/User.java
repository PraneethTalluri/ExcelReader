package com.praneeth.excel.reader.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private String name;

    private LocalDateTime dob;

    private double phoneNumber;
}
