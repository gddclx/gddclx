package com.example.demo.dto;

import lombok.Data;

@Data
public class ConfirmDeductionRequest {
    private String employeeId;
    private String type; // "late" or "early"
}
