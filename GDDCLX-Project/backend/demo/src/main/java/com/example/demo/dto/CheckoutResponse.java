package com.example.demo.dto;

/**
 * 签退响应DTO — 后端 → 前端
 * 包含签退结果以及系统自动判定的迟到/早退状态
 * 签到时间 > 9:00 → isLate = true
 * 签退时间 < 17:00 → isEarly = true
 */
public class CheckoutResponse {
    private boolean success;      // 是否签退成功
    private String message;       // 提示信息（含迟到/早退状态说明）
    private String checkoutTime;  // 签退时间字符串
    private boolean isLate;       // 是否迟到（系统自动判定）
    private boolean isEarly;      // 是否早退（系统自动判定）

    public CheckoutResponse() {}

    public CheckoutResponse(boolean success, String message, String checkoutTime, boolean isLate, boolean isEarly) {
        this.success = success;
        this.message = message;
        this.checkoutTime = checkoutTime;
        this.isLate = isLate;
        this.isEarly = isEarly;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getCheckoutTime() { return checkoutTime; }
    public boolean isLate() { return isLate; }
    public boolean isEarly() { return isEarly; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setCheckoutTime(String checkoutTime) { this.checkoutTime = checkoutTime; }
    public void setLate(boolean late) { isLate = late; }
    public void setEarly(boolean early) { isEarly = early; }
}
