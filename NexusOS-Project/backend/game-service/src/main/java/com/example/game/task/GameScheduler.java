package com.example.game.task;

import com.example.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameScheduler {

    @Autowired
    private GameService gameService;

    @Scheduled(cron = "0 0 0 * * *")
    public void dailySettlementAndGenerate() {
        gameService.settleYesterdayInvestments();
        gameService.generateTodayBankRates();
    }
}
