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

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private EmployeeMapper employeeMapper;

    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", data);
        map.put("code", 200);
        return map;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        return map;
    }

    private String resolveEmployeeId(String paramId) {
        if (paramId == null || paramId.isEmpty()) {
            return null;
        }
        // 查 employee 表，确认这个工号存在
        Employee emp = employeeMapper.selectByEmployeeId(paramId);
        if (emp != null) {
            return emp.getEmployeeId();  // 返回字符串 employee_id（如 "10000", "EMP-2028"）
        }
        // 没查到也返回原始值（兼容测试数据）
        return paramId;
    }

    private String getEmployeeId(HttpSession session, String paramEmployeeId) {
        if (paramEmployeeId != null && !paramEmployeeId.isEmpty()) {
            return resolveEmployeeId(paramEmployeeId);
        }

        Object employeeIdObj = session.getAttribute("employeeId");
        if (employeeIdObj == null) {
            employeeIdObj = session.getAttribute("userId");
        }

        if (employeeIdObj == null) {
            Object userInfoObj = session.getAttribute("userInfo");
            if (userInfoObj instanceof Employee) {
                Employee emp = (Employee) userInfoObj;
                String empId = emp.getEmployeeId();
                if (empId != null && !empId.isEmpty()) {
                    return empId;
                }
            }
        }

        if (employeeIdObj == null) {
            return null;
        }

        if (employeeIdObj instanceof String) {
            return resolveEmployeeId((String) employeeIdObj);
        }
        if (employeeIdObj instanceof Long) {
            return String.valueOf(employeeIdObj);
        }
        if (employeeIdObj instanceof Integer) {
            return String.valueOf(employeeIdObj);
        }

        return String.valueOf(employeeIdObj);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getGameStatus(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        GameStatusResponse status = gameService.getGameStatus(empId);
        return ResponseEntity.ok(ok(status));
    }

    @PostMapping("/collect")
    public ResponseEntity<?> collectCoins(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        CollectResponse response = gameService.collectCoins(empId);
        return ResponseEntity.ok(ok(response));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        UpgradeResponse response = gameService.upgrade(empId);
        return ResponseEntity.ok(ok(response));
    }

    @GetMapping("/bank/today")
    public ResponseEntity<?> getBankToday(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        BankTodayResponse response = gameService.getBankToday(empId);
        return ResponseEntity.ok(ok(response));
    }

    @PostMapping("/invest")
    public ResponseEntity<?> invest(HttpSession session,
            @RequestParam(required = false) String employeeId,
            @RequestBody InvestRequest request) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        InvestResponse response = gameService.invest(empId, request);
        return ResponseEntity.ok(ok(response));
    }

    @GetMapping("/rank")
    public ResponseEntity<?> getRank(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        RankResponse response = gameService.getRank(empId);
        return ResponseEntity.ok(ok(response));
    }

    @GetMapping("/sign/status")
    public ResponseEntity<?> getSignStatus(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        SignStatusResponse response = gameService.getSignStatus(empId);
        return ResponseEntity.ok(ok(response));
    }

    @GetMapping("/records")
    public ResponseEntity<?> getCoinRecords(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        List<CoinRecordResponse> records = gameService.getCoinRecords(empId);
        return ResponseEntity.ok(ok(records));
    }

    @PostMapping("/sign")
    public ResponseEntity<?> doSign(HttpSession session,
            @RequestParam(required = false) String employeeId) {
        String empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        SignResponse response = gameService.doSign(empId);
        return ResponseEntity.ok(ok(response));
    }
}
