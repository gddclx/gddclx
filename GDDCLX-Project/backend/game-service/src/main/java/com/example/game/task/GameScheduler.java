package com.example.game.task;

import com.example.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 游戏定时任务调度器
 * @Component 纳入Spring容器管理
 * @Scheduled 定义Cron表达式定时执行
 *
 * 每天0:00执行：
 * 1. settleYesterdayInvestments() — 结算昨天的理财投资（按当天利率计算收益）
 * 2. generateTodayBankRates() — 为今天生成新的随机利率
 */
@Component
public class GameScheduler {

    @Autowired
    private GameService gameService;

    /**
     * 每天0:00执行定时结算和利率生成
     * Cron表达式 "0 0 0 * * *" = 秒 分 时 日 月 周 = 每天0点0分0秒
     */
    @Scheduled(cron = "0 0 0 * * *")  // 每天凌晨0点
    public void dailySettlementAndGenerate() {
        gameService.settleYesterdayInvestments();  // 结算昨天投资
        gameService.generateTodayBankRates();      // 生成今天利率
    }
}
