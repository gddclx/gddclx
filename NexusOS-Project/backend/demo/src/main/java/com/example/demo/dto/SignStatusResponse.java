package com.example.demo.dto;

import lombok.Data;

@Data
public class SignStatusResponse {
    private Boolean canSign;
    private Integer signCount;
    private Long signReward;
    private String lastSignDate;
}
