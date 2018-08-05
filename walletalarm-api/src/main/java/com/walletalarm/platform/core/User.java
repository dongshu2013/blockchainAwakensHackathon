package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.sql.Timestamp;

public class User {
    private int userId;

    @NotBlank(message = "deviceId is required")
    private String deviceId;
    private String timezone;
    private String notificationCode;
    private OS os;
    private String version;
    private String comment;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;
    public User() {
    }

    public User(int userId, String deviceId, String timezone, String notificationCode, OS os, String version,
                String comment, Timestamp createdTime, Timestamp modifiedTime, boolean isActive) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.timezone = timezone;
        this.notificationCode = notificationCode;
        this.os = os;
        this.version = version;
        this.comment = comment;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.isActive = isActive;
    }

    @JsonProperty
    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    @JsonProperty
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    @JsonProperty
    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @JsonProperty
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @JsonProperty
    public String getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(String notificationCode) {
        this.notificationCode = notificationCode;
    }

    @JsonProperty
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
