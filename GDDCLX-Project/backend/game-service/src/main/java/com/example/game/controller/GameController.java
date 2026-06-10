package com.example.game.controller;

import com.example.game.domain.Employee;
import com.example.game.dto.*;
import com.example.game.mapper.EmployeeMapper;
import com.example.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏微服务 Controller — 端口 8081，路径 /api/game
 * 提供：金币状态、收集、升级、签到、银行理财、排行榜、金币流水
 * 通过 Nginx 反向代理对外暴露：http://域名/api/game/xxx → localhost:8081/api/game/xxx
 *
 * 特点：
 * 1. 使用 ResponseEntity<?> 返回（比 Map 多了HTTP状态码控制）
 * 2. 通过 HttpSession 获取当前登录用户（游戏功能需要登录态）
 * 3. 抽 ok()/error() 私有方法消除重复
 * 4. resolveEmployeeId() 校验工号是否在 employee 表中存在
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private EmployeeMapper employeeMapper;  // 用于校验工号存在性

    /** 成功响应 { code:200, data:... } */
    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", data);
        map.put("code", 200);
        return map;
    }

    /** 错误响应 { message:"..." } */
    private Map<String, Object> error(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        return map;
    }

    /**
     * 校验工号是否在employee表中存在
     * 存在 → 返回工号字符串
     * 不存在 → 仍返回原值（兼容测试数据）
     */
    private String resolveEmployeeId(String paramId) {
        if (paramId == null || paramId.isEmpty()) return null;
        Employee emp = employeeMapper.selectByEmployeeId(paramId);
        if (emp != null) return emp.getEmployeeId();
        return paramId;  // 降级兼容
    }

    /**
     * 多来源获取当前登录员工ID
     * 优先级：URL参数 > HttpSession的employeeId > HttpSession的userId > HttpSession的userInfo对象
     */
    private String getEmployeeId(HttpSession session, String paramEmployeeId) {
        // 优先级1：URL参数显式传递
        if (paramEmployeeId != null && !paramEmployeeId.isEmpty()) {
            return resolveEmployeeId(paramEmployeeId);
        }
        // 优先级2：Session中的employeeId
        Object employeeIdObj = session.getAttribute("employeeId");
        if (employeeIdObj == null) {
            employeeIdObj = session.getAttribute("userId");  // 优先级3：兼容旧key
        }
        // 优先级4：Session中的userInfo对象
        if (employeeIdObj == null) {
            Object userInfoObj = session.getAttribute("userInfo");
            if (userInfoObj instanceof Employee) {
                Employee emp = (Employee) userInfoObj;
                if (emp.getEmployeeId() != null && !emp.getEmployeeId().isEmpty()) {
                    return emp.getEmployeeId();
                }
            }
        }
        if (employeeIdObj == null) return null;
        // 类型转换
        if (employeeIdObj instanceof String) return resolveEmployeeId((String) employeeIdObj);
        if (employeeIdObj instanceof Long) return String.valueOf(employeeIdObj);
        if (employeeIdObj instanceof Integer) return String.valueOf(employeeIdObj);
        return String.valueOf(employeeIdObj);
    }

    /**
     * 获取游戏状态（金币、等级、待领取包数、今日投资状态等）
     * GET /api/game/status?employeeId=A001
     */
    @GetMapping("/status")
    public ResponseEntity<?> getGameStatus(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));  // 401未登录
        return ResponseEntity.ok(ok(gameService.getGameStatus(empId)));
    }

    /**
     * 领取累积的金币包
     * POST /api/game/collect?employeeId=A001
     * 每30分钟产1包（最多3包），每包金币 = 100 + coinLevel × 100
     */
    @PostMapping("/collect")
    public ResponseEntity<?> collectCoins(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.collectCoins(empId)));
    }

    /**
     * 升级金币等级
     * POST /api/game/upgrade?employeeId=A001
     * 升级费用 = (level + 1) × 1000（等差数列递增）
     */
    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.upgrade(empId)));
    }

    /**
     * 查询今日银行利率（上午/下午 × 3个部门）
     * GET /api/game/bank/today?employeeId=A001
     */
    @GetMapping("/bank/today")
    public ResponseEntity<?> getBankToday(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.getBankToday(empId)));
    }

    /**
     * 投资理财
     * POST /api/game/invest { optionType:1(HR)/2(研发)/3(销售), amount:100 }
     * 每天上/下午各一次，投资后金币立即扣除，次日0:00结算返还
     */
    @PostMapping("/invest")
    public ResponseEntity<?> invest(HttpSession session,
            @RequestParam(required = false) String employeeId,
            @RequestBody InvestRequest request) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.invest(empId, request)));
    }

    /**
     * 排行榜 TOP10 + 当前用户排名
     * GET /api/game/rank?employeeId=A001
     */
    @GetMapping("/rank")
    public ResponseEntity<?> getRank(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.getRank(empId)));
    }

    /**
     * 获取签到状态（是否已签、连续天数）
     * GET /api/game/sign/status?employeeId=A001
     */
    @GetMapping("/sign/status")
    public ResponseEntity<?> getSignStatus(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.getSignStatus(empId)));
    }

    /**
     * 金币流水记录
     * GET /api/game/records?employeeId=A001
     */
    @GetMapping("/records")
    public ResponseEntity<?> getCoinRecords(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.getCoinRecords(empId)));
    }

    /**
     * 执行每日签到 — 奖励200金币
     * POST /api/game/sign?employeeId=A001
     */
    @PostMapping("/sign")
    public ResponseEntity<?> doSign(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) return ResponseEntity.status(401).body(error("请先登录"));
        return ResponseEntity.ok(ok(gameService.doSign(empId)));
    }
}
