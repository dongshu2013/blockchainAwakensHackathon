package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IOSMessage {
    //    {
//        "notification": {
//        "body": "8 new ICO Added. Name 1, Name 2, Name 3...",
//                "title": "Weekly Notification",
//                "content_available": 1
//    },
//        "data": {
//        "ids": "51",
//        "idType": "tx",
//        "notificationId": "10"
//    },
//        "to": "cDGn5cPQAes:APA91bGEtjMfkTdR3FXg-CKiRyG1hFLUK-xDf4VkyV_80mEmoVdgFS-k1Z0s5TGRhx0dMPlHJL7WTX-BvH7zAd0USsHAkb58R1ankd0la6WDpvSd9zT4kDlYJ2D34cTeXIWRbBbN4-S9",
//            "priority": "high",
//            "content_available": true
//    }
    private IOSNotification notification;
    private IOSData data;
    private String to;
    private String[] registration_ids;
    private String priority;
    private boolean content_available;

    public IOSMessage() {
        content_available = true;
        priority = "high";
    }

    public IOSMessage(IOSNotification notification, IOSData data, String to) {
        doConstruct(notification, data);
        this.to = to;
    }

    public IOSMessage(IOSNotification notification, IOSData data, String[] registration_ids) {
        doConstruct(notification, data);
        this.registration_ids = registration_ids;
    }

    private void doConstruct(IOSNotification notification, IOSData data) {
        this.notification = notification;
        this.data = data;
        priority = "high";
        content_available = true;
    }

    @JsonProperty
    public IOSNotification getNotification() {
        return notification;
    }

    public void setNotification(IOSNotification notification) {
        this.notification = notification;
    }

    @JsonProperty
    public IOSData getData() {
        return data;
    }

    public void setData(IOSData data) {
        this.data = data;
    }

    @JsonProperty
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @JsonProperty
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @JsonProperty
    public boolean isContent_available() {
        return content_available;
    }

    public void setContent_available(boolean content_available) {
        this.content_available = content_available;
    }

    @JsonProperty
    public String[] getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(String[] registration_ids) {
        this.registration_ids = registration_ids;
    }
}