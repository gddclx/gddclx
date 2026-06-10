package com.example.game.service.impl;

import com.example.game.domain.EmployeeGame;
import com.example.game.domain.GameBankDaily;
import com.example.game.domain.GameCoinRecord;
import com.example.game.domain.GameInvestment;
import com.example.game.dto.*;
import com.example.game.mapper.*;
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

/**
 * 游戏服务实现类 — 全项目最大的Service（651行）
 * 提供：金币挂机系统 + 银行理财 + 签到 + 排行榜
 *
 * 核心数值：
 * - 每30分钟产1包金币（最多累积3包）
 * - 每包金币 = 100 + coinLevel（等级越高产越多）
 * - 升级费用 = (level + 1) × 1000（等差数列递增）
 * - 签到奖励 = 200金币
 * - 银行利率：上午(1.05/1.30/0.85) 下午(1.08/1.25/0.90) 每天随机洗牌
 * - 每天上/下午各可投资一次，次日0:00结算
 */
@Service
public class GameServiceImpl implements GameService {

    private static final int MAX_UNCLAIMED = 3;              // 最大累积包数
    private static final int COLLECT_INTERVAL_MINUTES = 30;   // 每包产间隔（分钟）
    private static final int BASE_INCOME_PER_COLLECT = 100;  // 每包基础金币
    private static final long SIGN_REWARD = 200;              // 签到奖励金币

    @Autowired private EmployeeGameMapper employeeGameMapper;
    @Autowired private GameInvestmentMapper gameInvestmentMapper;
    @Autowired private GameBankDailyMapper gameBankDailyMapper;
    @Autowired private GameRankMapper gameRankMapper;
    @Autowired private GameCoinRecordMapper gameCoinRecordMapper;
    @Autowired private com.example.game.mapper.GameSignMapper gameSignMapper;

    /** 获取游戏状态 — 含金币、等级、待领取包数、升级费用、下一包倒计时 */
    @Override
    public GameStatusResponse getGameStatus(String employeeId) {
        EmployeeGame game = getOrCreateGame(employeeId);  // 新用户自动初始化
        int unclaimedCount = calculateUnclaimedCount(game);  // 根据时间差计算
        long nextCollectSeconds = calculateNextCollectSeconds(game);

        GameStatusResponse response = new GameStatusResponse();
        response.setCoins(game.getCoins());
        response.setCoinLevel(game.getCoinLevel());
        response.setUnclaimedCount(unclaimedCount);
        response.setMaxUnclaimed(MAX_UNCLAIMED);
        response.setNextCollectSeconds(nextCollectSeconds);
        response.setUpgradeCost((long) (game.getCoinLevel() + 1) * 1000);  // 升级费 = (level+1)×1000
        response.setCurrentIncome(BASE_INCOME_PER_COLLECT + game.getCoinLevel());  // 每包产出
        return response;
    }

    /** 领取累积金币包 — 一次性领完所有可领取的包 */
    @Override
    @Transactional
    public CollectResponse collectCoins(String employeeId) {
        EmployeeGame game = getOrCreateGame(employeeId);
        int unclaimedCount = calculateUnclaimedCount(game);
        if (unclaimedCount <= 0) {
            CollectResponse response = new CollectResponse();
            response.setSuccess(false); response.setMessage("暂无可领取金币");
            response.setCoinsCollected(0L); response.setTotalCoins(game.getCoins());
            return response;
        }

        long incomePerCollect = BASE_INCOME_PER_COLLECT + game.getCoinLevel();  // 每包金币
        long coinsCollected = (long) unclaimedCount * incomePerCollect;         // 总领取额

        game.setCoins(game.getCoins() + coinsCollected);
        game.setUnclaimedCount(0);                         // 重置待领取计数
        game.setLastCollectTime(LocalDateTime.now());      // 更新领取时间
        employeeGameMapper.updateByEmployeeId(game);
        recordCoinChange(employeeId, "collect", coinsCollected, game.getCoins(), "领取金币 x" + unclaimedCount);

        CollectResponse response = new CollectResponse();
        response.setSuccess(true); response.setMessage("领取成功");
        response.setCoinsCollected(coinsCollected); response.setTotalCoins(game.getCoins());
        return response;
    }

    /** 升级金币等级 — 消耗金币提高每包产出 */
    @Override
    @Transactional
    public UpgradeResponse upgrade(String employeeId) {
        EmployeeGame game = getOrCreateGame(employeeId);
        long upgradeCost = (long) (game.getCoinLevel() + 1) * 1000;  // 升级费用公式

        if (game.getCoins() < upgradeCost) {
            UpgradeResponse response = new UpgradeResponse();
            response.setSuccess(false);
            response.setMessage("金币不足，升级需要 " + upgradeCost + " 金币");
            response.setCoinsSpent(0L); response.setTotalCoins(game.getCoins());
            response.setNewLevel(game.getCoinLevel());
            return response;
        }

        game.setCoins(game.getCoins() - upgradeCost);
        int newLevel = game.getCoinLevel() + 1;    // 等级+1
        game.setCoinLevel(newLevel);
        employeeGameMapper.updateByEmployeeId(game);
        recordCoinChange(employeeId, "upgrade", -upgradeCost, game.getCoins(), "升级到 Lv." + newLevel);

        UpgradeResponse response = new UpgradeResponse();
        response.setSuccess(true); response.setMessage("升级成功");
        response.setCoinsSpent(upgradeCost); response.setTotalCoins(game.getCoins());
        response.setNewLevel(game.getCoinLevel());
        return response;
    }

    /** 获取今日银行信息 — 利率+投资状态+昨日结算 */
    @Override
    public BankTodayResponse getBankToday(String employeeId) {
        settlePastInvestments(employeeId);  // 先处理过期未结算投资
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 确保今日利率已生成（懒加载：有人访问时生成）
        GameBankDaily todayBank = gameBankDailyMapper.findByDate(today);
        if (todayBank == null) { generateTodayBankRates(); todayBank = gameBankDailyMapper.findByDate(today); }

        BankTodayResponse response = new BankTodayResponse();
        // 3个部门选项
        List<BankTodayResponse.BankOption> options = new ArrayList<>();
        options.add(createBankOption(1, "人力部门", "fa-users"));
        options.add(createBankOption(2, "研发部门", "fa-code"));
        options.add(createBankOption(3, "销售部门", "fa-chart-line"));
        response.setOptions(options);

        // 判断当前时段（上午<12点）
        LocalDateTime now = LocalDateTime.now();
        int currentPeriod = now.getHour() < 12 ? 1 : 2;
        GameInvestment todayAm = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, today, 1);
        GameInvestment todayPm = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, today, 2);
        if (currentPeriod == 1 && todayAm != null) { response.setInvestedOption(todayAm.getOptionType()); response.setInvestedPeriod(1); }
        else if (currentPeriod == 2 && todayPm != null) { response.setInvestedOption(todayPm.getOptionType()); response.setInvestedPeriod(2); }
        response.setAmInvested(todayAm != null);
        response.setPmInvested(todayPm != null);

        // 昨日结算结果
        List<BankTodayResponse.SettlementResult> settlements = new ArrayList<>();
        GameBankDaily yesterdayBank = gameBankDailyMapper.findByDate(yesterday);
        if (yesterdayBank != null) {
            // ... 组装上/下午结算结果
        }
        if (!settlements.isEmpty()) response.setYesterdaySettlement(settlements.get(0));
        response.setYesterdaySettlements(settlements);
        response.setLastInvestment(getLastInvestmentResult(employeeId));
        return response;
    }

    /** 投资理财 — 每天上/下午各一次 */
    @Override
    @Transactional
    public InvestResponse invest(String employeeId, InvestRequest request) {
        EmployeeGame game = getOrCreateGame(employeeId);
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int periodType = now.getHour() < 12 ? 1 : 2;  // 自动判断时段
        String periodName = periodType == 1 ? "上午" : "下午";

        // 参数校验
        if (request.getAmount() == null || request.getAmount() <= 0) { /* 返回错误 */ }
        if (request.getOptionType() == null || request.getOptionType() < 1 || request.getOptionType() > 3) { /* 返回错误 */ }
        // 防重复投资
        GameInvestment existing = gameInvestmentMapper.findByEmployeeIdDateAndPeriod(employeeId, today, periodType);
        if (existing != null) { /* 返回"已投资" */ }
        // 金币不足
        if (game.getCoins() < request.getAmount()) { /* 返回"金币不足" */ }

        // 扣金币 + 插入投资记录（settled=0）
        game.setCoins(game.getCoins() - request.getAmount());
        employeeGameMapper.updateByEmployeeId(game);

        GameInvestment investment = new GameInvestment();
        investment.setEmployeeId(employeeId);
        investment.setAmount(request.getAmount());
        investment.setOptionType(request.getOptionType());
        investment.setInvestDate(today);
        investment.setPeriodType(periodType);
        investment.setSettled(0);  // 未结算
        gameInvestmentMapper.insert(investment);
        recordCoinChange(employeeId, "invest", -request.getAmount(), game.getCoins(),
                "投资" + getDepartmentName(request.getOptionType()) + " " + request.getAmount() + "金币");

        InvestResponse response = new InvestResponse();
        response.setSuccess(true); response.setMessage(periodName + "投资成功");
        response.setAmountInvested(request.getAmount());
        response.setDepartmentName(getDepartmentName(request.getOptionType()));
        response.setRemainingCoins(game.getCoins());
        return response;
    }

    /** 排行榜 TOP10 + 当前用户排名 */
    @Override
    public RankResponse getRank(String employeeId) {
        List<Map<String, Object>> top10Data = gameRankMapper.findTop10WithNames();
        // 计算第一名金币数（进度条最大值）
        Long maxCoins = 0L;
        for (Map<String, Object> row : top10Data) {
            Long coins = ((Number) row.get("coins")).longValue();
            if (coins > maxCoins) maxCoins = coins;
        }
        // 组装TOP10
        List<RankItemResponse> top10 = new ArrayList<>();
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
        // 当前用户排名
        Long userRank = gameRankMapper.getRankByEmployeeId(employeeId);
        Map<String, Object> userData = gameRankMapper.findByEmployeeIdWithName(employeeId);
        RankItemResponse currentUser = new RankItemResponse();
        currentUser.setRank(userRank.intValue() + 1);
        if (userData != null && userData.get("name") != null) currentUser.setName(userData.get("name").toString());
        else currentUser.setName("你");
        if (userData != null && userData.get("coins") != null) {
            currentUser.setCoins(((Number) userData.get("coins")).longValue());
            currentUser.setMaxCoins(Math.max(currentUser.getCoins(), maxCoins));
        }
        response.setCurrentUser(currentUser);
        return response;
    }

    /** 签到状态 */
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

    /** 执行签到 — 奖励200金币，连续签到天数追踪 */
    @Override
    @Transactional
    public SignResponse doSign(String employeeId) {
        getOrCreateGame(employeeId);
        EmployeeGame game = employeeGameMapper.findByEmployeeId(employeeId);
        LocalDate today = LocalDate.now();

        if (game.getLastSignDate() != null && game.getLastSignDate().equals(today)) {
            SignResponse response = new SignResponse();
            response.setSuccess(false); response.setMessage("今日已签到");
            response.setCoinsEarned(0L); response.setTotalCoins(game.getCoins());
            response.setSignCount(game.getSignCount() != null ? game.getSignCount() : 0);
            return response;
        }

        // 连续签到：昨天签了才累加，否则重置为1
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
        signParams.put("employeeId", employeeId); signParams.put("signDate", today); signParams.put("coinsEarned", SIGN_REWARD);
        gameSignMapper.insertSign(signParams);
        recordCoinChange(employeeId, "sign", SIGN_REWARD, game.getCoins(), "签到(连续" + newSignCount + "天)");

        SignResponse response = new SignResponse();
        response.setSuccess(true); response.setMessage("签到成功");
        response.setCoinsEarned(SIGN_REWARD); response.setTotalCoins(game.getCoins());
        response.setSignCount(newSignCount);
        return response;
    }

    /** 定时结算：每天0:00结算昨天所有未结算投资 */
    @Override
    @Transactional
    public void settleYesterdayInvestments() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<GameInvestment> unsettled = gameInvestmentMapper.findUnsettledByDate(yesterday);
        GameBankDaily yesterdayBank = gameBankDailyMapper.findByDate(yesterday);
        if (yesterdayBank == null || unsettled.isEmpty()) return;

        for (GameInvestment inv : unsettled) {
            BigDecimal rate = inv.getPeriodType() == 2
                    ? getPmRateByOption(yesterdayBank, inv.getOptionType())
                    : getRateByOption(yesterdayBank, inv.getOptionType());
            long resultCoins = BigDecimal.valueOf(inv.getAmount()).multiply(rate)
                    .setScale(0, RoundingMode.FLOOR).longValue();  // 投资额×利率，向下取整

            gameInvestmentMapper.settle(inv.getId(), resultCoins);
            employeeGameMapper.addCoins(inv.getEmployeeId(), resultCoins);
            EmployeeGame gameAfter = employeeGameMapper.findByEmployeeId(inv.getEmployeeId());
            recordCoinChange(inv.getEmployeeId(), "settlement", resultCoins,
                    gameAfter != null ? gameAfter.getCoins() : 0L, "结算" + getDepartmentName(inv.getOptionType()) + " 投资返还");
        }
    }

    /** 手动结算：处理当前用户的过期未结算投资 */
    @Override
    @Transactional
    public void settlePastInvestments(String employeeId) {
        List<GameInvestment> unsettled = gameInvestmentMapper.findUnsettledByEmployeeId(employeeId);
        // 判断是否应该结算：昨天或更早 → 结算；今天上午已过12点 → 结算上午的
        for (GameInvestment inv : unsettled) {
            boolean shouldSettle = inv.getInvestDate().isBefore(LocalDate.now());
            if (!shouldSettle && inv.getInvestDate().equals(LocalDate.now()) && inv.getPeriodType() == 1 && LocalDateTime.now().getHour() >= 12) {
                shouldSettle = true;
            }
            if (!shouldSettle) continue;
            // ... 结算逻辑
        }
    }

    /** 生成今日随机利率 — 3个部门 × 2时段，每天洗牌 */
    @Override
    @Transactional
    public void generateTodayBankRates() {
        LocalDate today = LocalDate.now();
        if (gameBankDailyMapper.findByDate(today) != null) return;  // 已存在则跳过

        // 上午利率池（1.05, 1.30, 0.85） → 随机分配给HR/研发/销售
        List<BigDecimal> amRates = Arrays.asList(new BigDecimal("1.05"), new BigDecimal("1.30"), new BigDecimal("0.85"));
        Collections.shuffle(amRates);
        // 下午利率池（1.08, 1.25, 0.90） → 随机分配
        List<BigDecimal> pmRates = Arrays.asList(new BigDecimal("1.08"), new BigDecimal("1.25"), new BigDecimal("0.90"));
        Collections.shuffle(pmRates);

        GameBankDaily bankDaily = new GameBankDaily();
        bankDaily.setBankDate(today);
        bankDaily.setHrRate(amRates.get(0)); bankDaily.setRdRate(amRates.get(1)); bankDaily.setSalesRate(amRates.get(2));
        bankDaily.setPmHrRate(pmRates.get(0)); bankDaily.setPmRdRate(pmRates.get(1)); bankDaily.setPmSalesRate(pmRates.get(2));
        gameBankDailyMapper.insert(bankDaily);
    }

    /** 金币流水记录 */
    @Override public List<CoinRecordResponse> getCoinRecords(String employeeId) {
        return gameCoinRecordMapper.findLast10ByEmployeeId(employeeId).stream().map(r -> {
            CoinRecordResponse resp = new CoinRecordResponse();
            resp.setId(r.getId()); resp.setType(r.getType()); resp.setAmount(r.getAmount());
            resp.setBalanceAfter(r.getBalanceAfter()); resp.setDescription(r.getDescription());
            resp.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
            return resp;
        }).collect(Collectors.toList());
    }

    // ========== 私有工具方法 ==========

    /** 获取或初始化员工游戏数据（懒初始化） */
    private EmployeeGame getOrCreateGame(String employeeId) {
        EmployeeGame game = employeeGameMapper.findByEmployeeId(employeeId);
        if (game == null) {
            game = new EmployeeGame();
            game.setEmployeeId(employeeId);
            game.setCoins(0L); game.setCoinLevel(0); game.setUnclaimedCount(0);
            game.setLastCollectTime(LocalDateTime.now());
            employeeGameMapper.insert(game);
        }
        return game;
    }

    /** 根据上次领取时间和当前时间计算可领取包数（每30分钟1包，最多3包） */
    private int calculateUnclaimedCount(EmployeeGame game) {
        if (game.getLastCollectTime() == null) return 0;
        long minutes = Duration.between(game.getLastCollectTime(), LocalDateTime.now()).toMinutes();
        return Math.min((int) (minutes / COLLECT_INTERVAL_MINUTES), MAX_UNCLAIMED);
    }

    /** 计算距离下一包可用还需多少秒 */
    private long calculateNextCollectSeconds(EmployeeGame game) {
        if (game.getLastCollectTime() == null) return 0;
        long seconds = Duration.between(game.getLastCollectTime(), LocalDateTime.now()).getSeconds();
        long elapsedIntervals = seconds / (COLLECT_INTERVAL_MINUTES * 60);
        if (elapsedIntervals >= MAX_UNCLAIMED) return 0;
        long nextCollectTimeSeconds = (elapsedIntervals + 1) * COLLECT_INTERVAL_MINUTES * 60;
        return Math.max(0, nextCollectTimeSeconds - seconds);
    }

    /** 获取部门中文名 */
    private String getDepartmentName(int optionType) {
        switch (optionType) { case 1: return "人力部门"; case 2: return "研发部门"; case 3: return "销售部门"; default: return "未知部门"; }
    }

    /** 获取上午利率 */
    private BigDecimal getRateByOption(GameBankDaily bank, int optionType) {
        switch (optionType) { case 1: return bank.getHrRate(); case 2: return bank.getRdRate(); case 3: return bank.getSalesRate(); default: return BigDecimal.ONE; }
    }

    /** 获取下午利率（pm字段为null时回退到上午利率） */
    private BigDecimal getPmRateByOption(GameBankDaily bank, int optionType) {
        switch (optionType) {
            case 1: return bank.getPmHrRate() != null ? bank.getPmHrRate() : bank.getHrRate();
            case 2: return bank.getPmRdRate() != null ? bank.getPmRdRate() : bank.getRdRate();
            case 3: return bank.getPmSalesRate() != null ? bank.getPmSalesRate() : bank.getSalesRate();
            default: return BigDecimal.ONE;
        }
    }

    /** 记录金币变动流水 */
    private void recordCoinChange(String employeeId, String type, Long amount, Long balanceAfter, String description) {
        GameCoinRecord record = new GameCoinRecord();
        record.setEmployeeId(employeeId); record.setType(type);
        record.setAmount(amount); record.setBalanceAfter(balanceAfter); record.setDescription(description);
        gameCoinRecordMapper.insert(record);
    }

    private BankTodayResponse.BankOption createBankOption(int type, String name, String icon) {
        BankTodayResponse.BankOption option = new BankTodayResponse.BankOption();
        option.setType(type); option.setName(name); option.setIcon(icon); return option;
    }

    private BankTodayResponse.LastInvestmentResult getLastInvestmentResult(String employeeId) {
        GameInvestment last = gameInvestmentMapper.findLastSettledInvestment(employeeId);
        if (last == null || last.getResultCoins() == null) return null;
        BankTodayResponse.LastInvestmentResult result = new BankTodayResponse.LastInvestmentResult();
        result.setDepartmentName(getDepartmentName(last.getOptionType()));
        result.setPeriodName(last.getPeriodType() == 1 ? "上午" : "下午");
        result.setAmount(last.getAmount()); result.setResultCoins(last.getResultCoins());
        GameBankDaily bankDaily = gameBankDailyMapper.findByDate(last.getInvestDate());
        BigDecimal rate = (bankDaily != null) ? (last.getPeriodType() == 2 ? getPmRateByOption(bankDaily, last.getOptionType()) : getRateByOption(bankDaily, last.getOptionType())) : BigDecimal.ONE;
        result.setRate(rate.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP) + "%");
        long coinsChange = last.getResultCoins() - last.getAmount();
        result.setCoinsChange(Math.abs(coinsChange)); result.setResult(coinsChange >= 0 ? "赚" : "亏");
        return result;
    }
}
