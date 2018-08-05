package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Login {
    private int loginId;
    private int userId;
    private String ipAddress;
    private Timestamp loginTime;

    public Login() {
    }

    public Login(int loginId, int userId, String ipAddress, Timestamp loginTime) {
        this.loginId = loginId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.loginTime = loginTime;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @JsonProperty
    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    @JsonProperty
    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }
}
