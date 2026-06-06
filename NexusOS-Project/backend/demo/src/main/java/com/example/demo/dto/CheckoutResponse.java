package com.example.demo.dto;

public class CheckoutResponse {
    private boolean success;
    private String message;
    private String checkoutTime;
    private boolean isLate;
    private boolean isEarly;

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
