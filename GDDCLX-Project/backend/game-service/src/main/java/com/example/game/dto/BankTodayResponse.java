package com.example.game.dto;

import lombok.Data;
import java.util.List;

/** 银行今日信息响应 — 利率选项+投资状态+结算结果 */
@Data
public class BankTodayResponse {
    private List<BankOption> options;                      // 3个部门选项
    private Integer investedOption;                        // 当前时段已投资部门
    private Integer investedPeriod;                        // 已投资时段
    private Boolean amInvested;                            // 上午是否已投资
    private Boolean pmInvested;                            // 下午是否已投资
    private SettlementResult yesterdaySettlement;          // 昨日结算首条
    private List<SettlementResult> yesterdaySettlements;   // 昨日全部结算
    private LastInvestmentResult lastInvestment;           // 上次投资结果

    @Data public static class BankOption {
        private Integer type;   // 1=HR, 2=研发, 3=销售
        private String name;    // 中文名
        private String icon;    // Font Awesome图标类名
    }
    @Data public static class SettlementResult {
        private String departmentName;  // 部门+时段名
        private String rate;            // 结算利率
        private Long coinsChange;       // 金币变化绝对值
        private String result;          // "赚"或"亏"
    }
    @Data public static class LastInvestmentResult {
        private String departmentName;
        private String periodName;      // 上午/下午
        private Long amount;            // 投资金额
        private Long resultCoins;       // 结算后金币
        private String rate;
        private Long coinsChange;
        private String result;
    }
}
