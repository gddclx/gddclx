package com.example.demo.dto;

import lombok.Data;

@Data
public class InvestRequest {
    private Long amount;
    private Integer optionType;
}
