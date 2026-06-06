package com.example.game.service.impl;

import com.example.game.domain.EmployeeGame;
import com.example.game.domain.GameBankDaily;
import com.example.game.domain.GameCoinRecord;
import com.example.game.domain.GameInvestment;
import com.example.game.dto.*;
import com.example.game.mapper.EmployeeGameMapper;
import com.example.game.mapper.GameBankDailyMapper;
import com.example.game.mapper.GameCoinRecordMapper;
import com.example.game.mapper.GameInvestmentMapper;
import com.example.game.mapper.GameRankMapper;
import com.example.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private static final int MAX_UNCLAIMED = 3;
    private static final int COLLECT_INTERVAL_MINUTES = 30;
    private static final int BASE_INCOME_PER_COLLECT = 100;

    @Autowired
    private EmployeeGameMapper employeeGameMapper;

    @Autowired
    private GameInvestmentMapper gameInvestmentMapper;

    @Autowired
    private GameBankDailyMapper gameBankDailyMapper;

    @Autowired
    private GameRankMapper gameRankMapper;

    @Autowired
    private GameCoinRecordMapper gameCoinRecordMapper;

    @Autowired
    private com.example.game.mapper.GameSignMapper gameSignMapper;

    private static final long SIGN_REWARD = 200;

    @Override
    public GameStatusResponse getGameStatus(String employeeId) {
        EmployeeGame game = getOrCreateGame(employeeId);

        int unclaimedCount = calculateUnclaimedCount(game);
        long nextCollectSeconds = calculateNextCollectSeconds(game);

        GameStatusResponse response = new GameStatusResponse();
        response.setCoins(game.getCoins());
        response.setCoinLevel(game.getCoinLevel());
        response.setUnclaimedCount(unclaimedCount);
        response.setMaxUnclaimed(MAX_UNCLAIMED);
        response.setNextCollectSeconds(nextCollectSeconds);
        response.setUpgradeCost((long) (game.getCoinLevel() + 1) * 1000);
        response.setCurrentIncome(BASE_INCOME_PER_COLLECT + game.getCoinLevel());

        return response;
    }

    @Override
    @Transactional
    public CollectResponse collectCoins(String employeeId) {
        EmployeeGame game = getOrCreateGame(employeeId);
        int unclaimedCount = calculateUnclaimedCount(game);

        if (unclaimedCount <= 0) {
            CollectResponse response = new CollectResponse();
            response.setSuccess(false);
            response.setMessage("暂无可领取金币");
            response.setCoinsCollected(0L);
            response.setTotalCoins(game.getCoins());
            return response;
        }

        long incomePerCollect = BASE_INCOME_PER_COLLECT + game.getCoinLevel();
        long coinsCollected = (long) unclaimedCount * incomePerCollect;

        game.setCoins(game.getCoins() + coinsCollected);
        game.setUnclaimedCount(0);
        game.setLastCollectTime(LocalDateTime.now());
        employeeGameMapper.updateByEmployeeId(game);
        recordCoinChange(employeeId, "collect", coinsCollected, game.getCoins(), "领取金币 x" + unclaimedCount);

        CollectResponse response = new CollectResponse();
        response.setSuccess(true);
        response.setMessage("领取成功");
        response.setCoinsCollected(coinsCollected);
        response.setTotalCoins(game.getCoins());
        return response;
    }

    @Override
    @Transactional
    public UpgradeResponse upgrade(String employeeId) {
        EmployeeGame game = getOrCreateGame(employeeId);
        long upgradeCost = (long) (game.getCoinLevel() + 1) * 1000;

        if (game.getCoins() < upgradeCost) {
            UpgradeResponse response = new UpgradeResponse();
            response.setSuccess(false);
            response.setMessage("金币不足，升级需要 " + upgradeCost + " 金币");
            response.setCoinsSpent(0L);
            response.setTotalCoins(game.getCoins());
            response.setNewLevel(game.getCoinLevel());
            return response;
        }

        game.setCoins(game.getCoins() - upgradeCost);
        int newLevel = game.getCoinLevel() + 1;
        game.setCoinLevel(newLevel);
        employeeGameMapper.updateByEmployeeId(game);
        recordCoinChange(employeeId, "upgrade", -upgradeCost, game.getCoins(), "升级到 Lv." + newLevel);

        UpgradeResponse response = new UpgradeResponse();
        response.setSuccess(true);
        response.setMessage("升级成功");
        response.setCoinsSpent(upgradeCost);
        response.setTotalCoins(game.getCoins());
        response.setNewLevel(game.getCoinLevel());
        return response;
    }

    @Override
    public BankTodayResponse getBankToday(String employeeId) {
        settlePastInvestments(employeeId);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        GameBankDaily todayBank = gameBankDailyMapper.findByDate(today);
        if (todayBank == null) {
            generateTodayBankRates();
            todayBank = gameBankDailyMapper.findByDate(today);
        }

        BankTodayResponse response = new BankTodayResponse();

        List<BankTodayResponse.BankOption> options = new ArrayList<>();
        options.add(createBankOption(1, "人力部门", "fa-users"));
        options.add(createBankOption(2, "研发部门", "fa-code"));
        options.add(createBankOption(3, "销售部门", "fa-chart-line"));
        response.setOptions(options);

        LocalDateTime now = LocalDateTime.now();
        int currentPeriod = now.getHour() < 12 ? 1 : 2;
        GameInvestment todayAmInvestment = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, today, 1);
        GameInvestment todayPmInvestment = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, today, 2);
        
        if (currentPeriod == 1) {
            if (todayAmInvestment != null) {
                response.setInvestedOption(todayAmInvestment.getOptionType());
                response.setInvestedPeriod(1);
            }
        } else {
            if (todayPmInvestment != null) {
                response.setInvestedOption(todayPmInvestment.getOptionType());
                response.setInvestedPeriod(2);
            }
        }
        
        response.setAmInvested(todayAmInvestment != null);
        response.setPmInvested(todayPmInvestment != null);

        List<BankTodayResponse.SettlementResult> settlements = new ArrayList<>();
        GameInvestment yesterdayAmInvestment = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, yesterday, 1);
        GameInvestment yesterdayPmInvestment = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, yesterday, 2);
        
        GameBankDaily yesterdayBank = gameBankDailyMapper.findByDate(yesterday);
        if (yesterdayBank != null) {
            if (yesterdayAmInvestment != null && yesterdayAmInvestment.getSettled() == 1 && yesterdayAmInvestment.getResultCoins() != null) {
                BigDecimal rate = getRateByOption(yesterdayBank, yesterdayAmInvestment.getOptionType());
                long coinsChange = yesterdayAmInvestment.getResultCoins() - yesterdayAmInvestment.getAmount();
                String result = coinsChange >= 0 ? "赚" : "亏";

                BankTodayResponse.SettlementResult settlement = new BankTodayResponse.SettlementResult();
                settlement.setDepartmentName("上午-" + getDepartmentName(yesterdayAmInvestment.getOptionType()));
                settlement.setRate(rate.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP) + "%");
                settlement.setCoinsChange(Math.abs(coinsChange));
                settlement.setResult(result);
                settlements.add(settlement);
            }
            
            if (yesterdayPmInvestment != null && yesterdayPmInvestment.getSettled() == 1 && yesterdayPmInvestment.getResultCoins() != null) {
                BigDecimal rate = getPmRateByOption(yesterdayBank, yesterdayPmInvestment.getOptionType());
                long coinsChange = yesterdayPmInvestment.getResultCoins() - yesterdayPmInvestment.getAmount();
                String result = coinsChange >= 0 ? "赚" : "亏";

                BankTodayResponse.SettlementResult settlement = new BankTodayResponse.SettlementResult();
                settlement.setDepartmentName("下午-" + getDepartmentName(yesterdayPmInvestment.getOptionType()));
                settlement.setRate(rate.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP) + "%");
                settlement.setCoinsChange(Math.abs(coinsChange));
                settlement.setResult(result);
                settlements.add(settlement);
            }
        }
        
        if (!settlements.isEmpty()) {
            response.setYesterdaySettlement(settlements.get(0));
        }
        response.setYesterdaySettlements(settlements);

        response.setLastInvestment(getLastInvestmentResult(employeeId));

        return response;
    }
    
    private BankTodayResponse.LastInvestmentResult getLastInvestmentResult(String employeeId) {
        GameInvestment lastInvestment = gameInvestmentMapper.findLastSettledInvestment(employeeId);
        if (lastInvestment == null || lastInvestment.getResultCoins() == null) {
            return null;
        }
        
        BankTodayResponse.LastInvestmentResult result = new BankTodayResponse.LastInvestmentResult();
        result.setDepartmentName(getDepartmentName(lastInvestment.getOptionType()));
        result.setPeriodName(lastInvestment.getPeriodType() == 1 ? "上午" : "下午");
        result.setAmount(lastInvestment.getAmount());
        result.setResultCoins(lastInvestment.getResultCoins());
        
        LocalDate investDate = lastInvestment.getInvestDate();
        GameBankDaily bankDaily = gameBankDailyMapper.findByDate(investDate);
        BigDecimal rate;
        if (bankDaily != null) {
            rate = lastInvestment.getPeriodType() == 2 ? 
                getPmRateByOption(bankDaily, lastInvestment.getOptionType()) :
                getRateByOption(bankDaily, lastInvestment.getOptionType());
            result.setRate(rate.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP) + "%");
        } else {
            rate = BigDecimal.ONE;
            result.setRate("100%");
        }
        
        long coinsChange = lastInvestment.getResultCoins() - lastInvestment.getAmount();
        result.setCoinsChange(Math.abs(coinsChange));
        result.setResult(coinsChange >= 0 ? "赚" : "亏");
        
        return result;
    }

    @Override
    @Transactional
    public InvestResponse invest(String employeeId, InvestRequest request) {
        EmployeeGame game = getOrCreateGame(employeeId);
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int periodType = now.getHour() < 12 ? 1 : 2;
        String periodName = periodType == 1 ? "上午" : "下午";

        if (request.getAmount() == null || request.getAmount() <= 0) {
            InvestResponse response = new InvestResponse();
            response.setSuccess(false);
            response.setMessage("请输入正确的投资金额");
            return response;
        }

        if (request.getOptionType() == null || request.getOptionType() < 1 || request.getOptionType() > 3) {
            InvestResponse response = new InvestResponse();
            response.setSuccess(false);
            response.setMessage("请选择正确的投资部门");
            return response;
        }

        GameInvestment existing = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, today, periodType);
        if (existing != null) {
            InvestResponse response = new InvestResponse();
            response.setSuccess(false);
            response.setMessage("今日" + periodName + "已投资，请" + (periodType == 1 ? "下午" : "明日") + "再来");
            return response;
        }

        if (game.getCoins() < request.getAmount()) {
            InvestResponse response = new InvestResponse();
            response.setSuccess(false);
            response.setMessage("金币不足，当前拥有 " + game.getCoins() + " 金币");
            return response;
        }

        game.setCoins(game.getCoins() - request.getAmount());
        employeeGameMapper.updateByEmployeeId(game);

        GameInvestment investment = new GameInvestment();
        investment.setEmployeeId(employeeId);
        investment.setAmount(request.getAmount());
        investment.setOptionType(request.getOptionType());
        investment.setInvestDate(today);
        investment.setPeriodType(periodType);
        investment.setSettled(0);
        gameInvestmentMapper.insert(investment);
        recordCoinChange(employeeId, "invest", -request.getAmount(), game.getCoins(),
                "投资" + getDepartmentName(request.getOptionType()) + " " + request.getAmount() + "金币");

        InvestResponse response = new InvestResponse();
        response.setSuccess(true);
        response.setMessage(periodName + "投资成功");
        response.setAmountInvested(request.getAmount());
        response.setDepartmentName(getDepartmentName(request.getOptionType()));
        response.setRemainingCoins(game.getCoins());
        return response;
    }

    @Override
    public RankResponse getRank(String employeeId) {
        List<Map<String, Object>> top10Data = gameRankMapper.findTop10WithNames();

        List<RankItemResponse> top10 = new ArrayList<>();
        Long maxCoins = 0L;
        for (Map<String, Object> row : top10Data) {
            Long coins = ((Number) row.get("coins")).longValue();
            if (coins > maxCoins) maxCoins = coins;
        }

        int rank = 1;
        for (Map<String, Object> row : top10Data) {
            RankItemResponse item = new RankItemResponse();
            item.setRank(rank++);
            item.setName(row.get("name") != null ? row.get("name").toString() : "未知");
            item.setCoins(((Number) row.get("coins")).longValue());
            item.setMaxCoins(maxCoins);
            top10.add(item);
        }

        RankResponse response = new RankResponse();
        response.setTop10(top10);

        Long userRank = gameRankMapper.getRankByEmployeeId(employeeId);
        Map<String, Object> userData = gameRankMapper.findByEmployeeIdWithName(employeeId);

        RankItemResponse currentUser = new RankItemResponse();
        currentUser.setRank(userRank.intValue() + 1);
        if (userData != null && userData.get("name") != null) {
            currentUser.setName(userData.get("name").toString());
        } else {
            currentUser.setName("你");
        }
        if (userData != null && userData.get("coins") != null) {
            currentUser.setCoins(((Number) userData.get("coins")).longValue());
            if (currentUser.getCoins() > maxCoins) {
                currentUser.setMaxCoins(currentUser.getCoins());
            } else {
                currentUser.setMaxCoins(maxCoins);
            }
        } else {
            currentUser.setCoins(0L);
            currentUser.setMaxCoins(maxCoins > 0 ? maxCoins : 1L);
        }
        response.setCurrentUser(currentUser);

        return response;
    }

    @Override
    public SignStatusResponse getSignStatus(String employeeId) {
        getOrCreateGame(employeeId);
        EmployeeGame game = employeeGameMapper.findByEmployeeId(employeeId);
        LocalDate today = LocalDate.now();

        SignStatusResponse response = new SignStatusResponse();
        response.setCanSign(game.getLastSignDate() == null || !game.getLastSignDate().equals(today));
        response.setSignCount(game.getSignCount() != null ? game.getSignCount() : 0);
        response.setSignReward(SIGN_REWARD);
        response.setLastSignDate(game.getLastSignDate() != null ? game.getLastSignDate().toString() : null);

        return response;
    }

    @Override
    @Transactional
    public SignResponse doSign(String employeeId) {
        getOrCreateGame(employeeId);
        EmployeeGame game = employeeGameMapper.findByEmployeeId(employeeId);
        LocalDate today = LocalDate.now();

        if (game.getLastSignDate() != null && game.getLastSignDate().equals(today)) {
            SignResponse response = new SignResponse();
            response.setSuccess(false);
            response.setMessage("今日已签到");
            response.setCoinsEarned(0L);
            response.setTotalCoins(game.getCoins());
            response.setSignCount(game.getSignCount() != null ? game.getSignCount() : 0);
            return response;
        }

        int newSignCount = 1;
        if (game.getLastSignDate() != null && game.getLastSignDate().equals(today.minusDays(1))) {
            newSignCount = (game.getSignCount() != null ? game.getSignCount() : 0) + 1;
        }

        game.setLastSignDate(today);
        game.setSignCount(newSignCount);
        game.setCoins(game.getCoins() + SIGN_REWARD);
        employeeGameMapper.updateSignInfo(employeeId, today, newSignCount);
        employeeGameMapper.addCoins(employeeId, SIGN_REWARD);

        Map<String, Object> signParams = new HashMap<>();
        signParams.put("employeeId", employeeId);
        signParams.put("signDate", today);
        signParams.put("coinsEarned", SIGN_REWARD);
        gameSignMapper.insertSign(signParams);
        recordCoinChange(employeeId, "sign", SIGN_REWARD, game.getCoins(), "签到(连续" + newSignCount + "天)");

        SignResponse response = new SignResponse();
        response.setSuccess(true);
        response.setMessage("签到成功");
        response.setCoinsEarned(SIGN_REWARD);
        response.setTotalCoins(game.getCoins());
        response.setSignCount(newSignCount);

        return response;
    }

    @Override
    @Transactional
    public void settleYesterdayInvestments() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<GameInvestment> unsettled = gameInvestmentMapper.findUnsettledByDate(yesterday);
        GameBankDaily yesterdayBank = gameBankDailyMapper.findByDate(yesterday);

        if (yesterdayBank == null || unsettled.isEmpty()) {
            return;
        }

        for (GameInvestment investment : unsettled) {
            BigDecimal rate;
            if (investment.getPeriodType() == 2) {
                rate = getPmRateByOption(yesterdayBank, investment.getOptionType());
            } else {
                rate = getRateByOption(yesterdayBank, investment.getOptionType());
            }
            long resultCoins = BigDecimal.valueOf(investment.getAmount())
                    .multiply(rate)
                    .setScale(0, RoundingMode.FLOOR)
                    .longValue();

            investment.setResultCoins(resultCoins);
            gameInvestmentMapper.settle(investment.getId(), resultCoins);

            employeeGameMapper.addCoins(investment.getEmployeeId(), resultCoins);

            EmployeeGame gameAfter = employeeGameMapper.findByEmployeeId(investment.getEmployeeId());
            String deptName = getDepartmentName(investment.getOptionType());
            recordCoinChange(investment.getEmployeeId(), "settlement", resultCoins,
                    gameAfter != null ? gameAfter.getCoins() : 0L,
                    "结算" + deptName + " 投资返还");
        }
    }

    @Override
    @Transactional
    public void settlePastInvestments(String employeeId) {
        List<GameInvestment> unsettled = gameInvestmentMapper.findUnsettledByEmployeeId(employeeId);
        if (unsettled.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        int currentHour = now.getHour();

        for (GameInvestment investment : unsettled) {
            LocalDate invDate = investment.getInvestDate();
            int periodType = investment.getPeriodType();

            boolean shouldSettle = false;
            if (invDate.isBefore(today)) {
                shouldSettle = true;
            } else if (invDate.equals(today)) {
                if (periodType == 1 && currentHour >= 12) {
                    shouldSettle = true;
                }
            }

            if (!shouldSettle) continue;

            GameBankDaily bankDaily = gameBankDailyMapper.findByDate(invDate);
            if (bankDaily == null) continue;

            BigDecimal rate = (periodType == 2)
                    ? getPmRateByOption(bankDaily, investment.getOptionType())
                    : getRateByOption(bankDaily, investment.getOptionType());

            long resultCoins = BigDecimal.valueOf(investment.getAmount())
                    .multiply(rate)
                    .setScale(0, RoundingMode.FLOOR)
                    .longValue();

            gameInvestmentMapper.settle(investment.getId(), resultCoins);
            employeeGameMapper.addCoins(employeeId, resultCoins);

            EmployeeGame gameAfter = employeeGameMapper.findByEmployeeId(employeeId);
            String deptName = getDepartmentName(investment.getOptionType());
            String periodName = periodType == 1 ? "上午" : "下午";
            recordCoinChange(employeeId, "settlement", resultCoins,
                    gameAfter != null ? gameAfter.getCoins() : 0L,
                    "结算" + periodName + deptName + " 投资返还");
        }
    }

    @Override
    public List<CoinRecordResponse> getCoinRecords(String employeeId) {
        List<GameCoinRecord> records = gameCoinRecordMapper.findLast10ByEmployeeId(employeeId);
        return records.stream().map(r -> {
            CoinRecordResponse resp = new CoinRecordResponse();
            resp.setId(r.getId());
            resp.setType(r.getType());
            resp.setAmount(r.getAmount());
            resp.setBalanceAfter(r.getBalanceAfter());
            resp.setDescription(r.getDescription());
            resp.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void generateTodayBankRates() {
        LocalDate today = LocalDate.now();

        GameBankDaily existing = gameBankDailyMapper.findByDate(today);
        if (existing != null) {
            return;
        }

        List<BigDecimal> amRates = Arrays.asList(
                new BigDecimal("1.05"),
                new BigDecimal("1.30"),
                new BigDecimal("0.85")
        );
        Collections.shuffle(amRates);

        List<BigDecimal> pmRates = Arrays.asList(
                new BigDecimal("1.08"),
                new BigDecimal("1.25"),
                new BigDecimal("0.90")
        );
        Collections.shuffle(pmRates);

        GameBankDaily bankDaily = new GameBankDaily();
        bankDaily.setBankDate(today);
        bankDaily.setHrRate(amRates.get(0));
        bankDaily.setRdRate(amRates.get(1));
        bankDaily.setSalesRate(amRates.get(2));
        bankDaily.setPmHrRate(pmRates.get(0));
        bankDaily.setPmRdRate(pmRates.get(1));
        bankDaily.setPmSalesRate(pmRates.get(2));

        gameBankDailyMapper.insert(bankDaily);
    }

    private EmployeeGame getOrCreateGame(String employeeId) {
        EmployeeGame game = employeeGameMapper.findByEmployeeId(employeeId);
        if (game == null) {
            game = new EmployeeGame();
            game.setEmployeeId(employeeId);
            game.setCoins(0L);
            game.setCoinLevel(0);
            game.setUnclaimedCount(0);
            game.setLastCollectTime(LocalDateTime.now());
            employeeGameMapper.insert(game);
        }
        return game;
    }

    private int calculateUnclaimedCount(EmployeeGame game) {
        if (game.getLastCollectTime() == null) {
            return 0;
        }

        Duration duration = Duration.between(game.getLastCollectTime(), LocalDateTime.now());
        long minutes = duration.toMinutes();
        int count = (int) (minutes / COLLECT_INTERVAL_MINUTES);

        return Math.min(count, MAX_UNCLAIMED);
    }

    private long calculateNextCollectSeconds(EmployeeGame game) {
        if (game.getLastCollectTime() == null) {
            return 0;
        }

        Duration duration = Duration.between(game.getLastCollectTime(), LocalDateTime.now());
        long seconds = duration.getSeconds();
        long elapsedIntervals = seconds / (COLLECT_INTERVAL_MINUTES * 60);

        if (elapsedIntervals >= MAX_UNCLAIMED) {
            return 0;
        }

        long nextCollectTimeSeconds = (elapsedIntervals + 1) * COLLECT_INTERVAL_MINUTES * 60;
        long remainingSeconds = nextCollectTimeSeconds - seconds;

        return Math.max(0, remainingSeconds);
    }

    private BankTodayResponse.BankOption createBankOption(int type, String name, String icon) {
        BankTodayResponse.BankOption option = new BankTodayResponse.BankOption();
        option.setType(type);
        option.setName(name);
        option.setIcon(icon);
        return option;
    }

    private String getDepartmentName(int optionType) {
        switch (optionType) {
            case 1: return "人力部门";
            case 2: return "研发部门";
            case 3: return "销售部门";
            default: return "未知部门";
        }
    }

    private BigDecimal getRateByOption(GameBankDaily bank, int optionType) {
        switch (optionType) {
            case 1: return bank.getHrRate();
            case 2: return bank.getRdRate();
            case 3: return bank.getSalesRate();
            default: return BigDecimal.ONE;
        }
    }
    
    private BigDecimal getPmRateByOption(GameBankDaily bank, int optionType) {
        switch (optionType) {
            case 1:
                return bank.getPmHrRate() != null ? bank.getPmHrRate() : bank.getHrRate();
            case 2:
                return bank.getPmRdRate() != null ? bank.getPmRdRate() : bank.getRdRate();
            case 3:
                return bank.getPmSalesRate() != null ? bank.getPmSalesRate() : bank.getSalesRate();
            default: return BigDecimal.ONE;
        }
    }

    private void recordCoinChange(String employeeId, String type, Long amount, Long balanceAfter, String description) {
        GameCoinRecord record = new GameCoinRecord();
        record.setEmployeeId(employeeId);
        record.setType(type);
        record.setAmount(amount);
        record.setBalanceAfter(balanceAfter);
        record.setDescription(description);
        gameCoinRecordMapper.insert(record);
    }
}
