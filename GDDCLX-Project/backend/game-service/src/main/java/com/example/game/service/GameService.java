package com.example.game.service;

import com.example.game.dto.*;
import java.util.List;

/** 游戏服务接口 — 金币系统 + 银行理财 + 签到 + 排行榜 */
public interface GameService {
    GameStatusResponse getGameStatus(String employeeId);      // 获取游戏状态
    CollectResponse collectCoins(String employeeId);          // 领取累积金币
    UpgradeResponse upgrade(String employeeId);               // 升级金币等级
    BankTodayResponse getBankToday(String employeeId);        // 今日银行利率+投资状态
    InvestResponse invest(String employeeId, InvestRequest request); // 投资理财
    RankResponse getRank(String employeeId);                  // 排行榜TOP10+自己
    SignStatusResponse getSignStatus(String employeeId);       // 签到状态
    SignResponse doSign(String employeeId);                   // 执行签到
    List<CoinRecordResponse> getCoinRecords(String employeeId); // 金币流水
    void settlePastInvestments(String employeeId);             // 手动结算过期投资
    void settleYesterdayInvestments();                         // 定时结算昨天投资
    void generateTodayBankRates();                             // 生成今日随机利率
}
