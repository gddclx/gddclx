package com.example.demo.service;

import com.example.demo.dto.CheckinResponse;
import com.example.demo.dto.CheckoutResponse;
import com.example.demo.dto.CompanyAttendanceResponse;
import com.example.demo.dto.MonthAttendanceResponse;
import com.example.demo.dto.TodayStatusResponse;
import com.example.demo.dto.WeekAttendanceResponse;

/**
 * 考勤服务接口 — 签到签退 + 统计查询
 * 实现类：AttendanceServiceImpl
 * 迟到判定：签到 > 9:00 / 早退判定：签退 < 17:00
 */
public interface AttendanceService {
    /** 签到 — 每日唯一约束防重复 */
    CheckinResponse checkin(String userId);

    /** 签退 — 自动判定迟到/早退并更新数据库 */
    CheckoutResponse checkout(String userId);

    /** 全公司今日考勤概况（已打卡人数/总人数/出勤率） */
    CompanyAttendanceResponse getCompanyTodayAttendance();

    /** 某人今日打卡状态 */
    TodayStatusResponse getTodayStatus(String userId);

    /** 本周考勤（7天数组 + 连续天数 + 等级） */
    WeekAttendanceResponse getWeekAttendance(String userId);

    /** 本月考勤（应出勤工作日天数 + 实际打卡天数 + 出勤率） */
    MonthAttendanceResponse getMonthAttendance(String userId);
}
