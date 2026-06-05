package com.example.demo.service;

import com.example.demo.dto.*;
import java.util.List;

public interface GameService {

    GameStatusResponse getGameStatus(Long employeeId);

    CollectResponse collectCoins(Long employeeId);

    UpgradeResponse upgrade(Long employeeId);

    BankTodayResponse getBankToday(Long employeeId);

    InvestResponse invest(Long employeeId, InvestRequest request);

    RankResponse getRank(Long employeeId);

    SignStatusResponse getSignStatus(Long employeeId);

    SignResponse doSign(Long employeeId);

    void settleYesterdayInvestments();

    void generateTodayBankRates();
}
