package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Notification {
    private int notificationId;
    private int userId;
    private String message;
    private String data;
    private NotificationStatus notificationStatus;
    private NotificationType notificationType;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;

    public Notification() {
    }

    public Notification(int notificationId, int userId, String message, String data, NotificationStatus notificationStatus,
                        NotificationType notificationType, Timestamp createdTime, Timestamp modifiedTime, boolean isActive) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.data = data;
        this.notificationStatus = notificationStatus;
        this.notificationType = notificationType;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.isActive = isActive;
    }


    @JsonProperty
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
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
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @JsonProperty
    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
}