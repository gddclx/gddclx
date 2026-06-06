package com.example.game.service;

import com.example.game.dto.*;
import java.util.List;

public interface GameService {

    GameStatusResponse getGameStatus(String employeeId);

    CollectResponse collectCoins(String employeeId);

    UpgradeResponse upgrade(String employeeId);

    BankTodayResponse getBankToday(String employeeId);

    InvestResponse invest(String employeeId, InvestRequest request);

    RankResponse getRank(String employeeId);

    SignStatusResponse getSignStatus(String employeeId);

    SignResponse doSign(String employeeId);

    List<CoinRecordResponse> getCoinRecords(String employeeId);

    void settlePastInvestments(String employeeId);

    void settleYesterdayInvestments();

    void generateTodayBankRates();
}
