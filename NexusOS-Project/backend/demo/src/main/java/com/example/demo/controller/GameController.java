package com.example.demo.controller;

import com.example.demo.domain.Employee;
import com.example.demo.dto.*;
import com.example.demo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

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

    private Long getEmployeeId(HttpSession session, Long paramEmployeeId) {
        if (paramEmployeeId != null) {
            return paramEmployeeId;
        }

        Object employeeIdObj = session.getAttribute("employeeId");
        if (employeeIdObj == null) {
            employeeIdObj = session.getAttribute("userId");
        }

        if (employeeIdObj == null) {
            Object userInfoObj = session.getAttribute("userInfo");
            if (userInfoObj instanceof Employee) {
                String empId = ((Employee) userInfoObj).getEmployeeId();
                if (empId != null) {
                    try {
                        employeeIdObj = Long.parseLong(empId);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        if (employeeIdObj == null) {
            return null;
        }

        if (employeeIdObj instanceof Long) {
            return (Long) employeeIdObj;
        } else if (employeeIdObj instanceof Integer) {
            return ((Integer) employeeIdObj).longValue();
        } else if (employeeIdObj instanceof String) {
            try {
                return Long.parseLong((String) employeeIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getGameStatus(HttpSession session,
            @RequestParam(required = false) Long employeeId) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        GameStatusResponse status = gameService.getGameStatus(empId);
        return ResponseEntity.ok(ok(status));
    }

    @PostMapping("/collect")
    public ResponseEntity<?> collectCoins(HttpSession session,
            @RequestParam(required = false) Long employeeId) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        CollectResponse response = gameService.collectCoins(empId);
        return ResponseEntity.ok(ok(response));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade(HttpSession session,
            @RequestParam(required = false) Long employeeId) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        UpgradeResponse response = gameService.upgrade(empId);
        return ResponseEntity.ok(ok(response));
    }

    @GetMapping("/bank/today")
    public ResponseEntity<?> getBankToday(HttpSession session,
            @RequestParam(required = false) Long employeeId) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        BankTodayResponse response = gameService.getBankToday(empId);
        return ResponseEntity.ok(ok(response));
    }

    @PostMapping("/invest")
    public ResponseEntity<?> invest(HttpSession session,
            @RequestParam(required = false) Long employeeId,
            @RequestBody InvestRequest request) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        InvestResponse response = gameService.invest(empId, request);
        return ResponseEntity.ok(ok(response));
    }

    @GetMapping("/rank")
    public ResponseEntity<?> getRank(HttpSession session,
            @RequestParam(required = false) Long employeeId) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        RankResponse response = gameService.getRank(empId);
        return ResponseEntity.ok(ok(response));
    }

    @GetMapping("/sign/status")
    public ResponseEntity<?> getSignStatus(HttpSession session,
            @RequestParam(required = false) Long employeeId) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        SignStatusResponse response = gameService.getSignStatus(empId);
        return ResponseEntity.ok(ok(response));
    }

    @PostMapping("/sign")
    public ResponseEntity<?> doSign(HttpSession session,
            @RequestParam(required = false) Long employeeId) {
        Long empId = getEmployeeId(session, employeeId);
        if (empId == null) {
            return ResponseEntity.status(401).body(error("请先登录"));
        }

        SignResponse response = gameService.doSign(empId);
        return ResponseEntity.ok(ok(response));
    }
}
