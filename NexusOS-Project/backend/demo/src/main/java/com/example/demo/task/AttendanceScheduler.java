package com.example.demo.task;

import com.example.demo.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AttendanceScheduler {

    @Autowired
    private PerformanceService performanceService;

    // 每天凌晨 1:00 自动标记昨天未签退的员工为早退
    @Scheduled(cron = "0 0 1 * * *")
    public void autoMarkEarlyLeave() {
        performanceService.autoMarkEarlyLeave();
    }
}
