package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSetting {
    private int userId;
    private String notificationCode;
    private boolean value;
    private OS os;

    public UserSetting() {
    }

    public UserSetting(int userId, String notificationCode, boolean value, OS os) {
        this.userId = userId;
        this.notificationCode = notificationCode;
        this.value = value;
        this.os = os;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @JsonProperty
    public String getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(String notificationCode) {
        this.notificationCode = notificationCode;
    }

    @JsonProperty
    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }
}
