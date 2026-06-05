package com.example.demo.dto;

import lombok.Data;

@Data
public class InvestResponse {
    private boolean success;
    private String message;
    private Long amountInvested;
    private String departmentName;
    private Long remainingCoins;
}
