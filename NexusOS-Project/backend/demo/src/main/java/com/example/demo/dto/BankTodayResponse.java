package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class BankTodayResponse {
    private List<BankOption> options;
    private Integer investedOption;
    private Integer investedPeriod;
    private Boolean amInvested;
    private Boolean pmInvested;
    private SettlementResult yesterdaySettlement;
    private List<SettlementResult> yesterdaySettlements;
    private LastInvestmentResult lastInvestment;

    @Data
    public static class BankOption {
        private Integer type;
        private String name;
        private String icon;
    }

    @Data
    public static class SettlementResult {
        private String departmentName;
        private String rate;
        private Long coinsChange;
        private String result;
    }

    @Data
    public static class LastInvestmentResult {
        private String departmentName;
        private String periodName;
        private Long amount;
        private Long resultCoins;
        private String rate;
        private Long coinsChange;
        private String result;
    }
}
