package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IOSNotification {
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

    private String body;
    private String title;
    private int content_available;

    public IOSNotification() {
    }

    public IOSNotification(String body, String title) {
        this.body = body;
        this.title = title;
        this.content_available = 1;
    }

    @JsonProperty
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public int getContent_available() {
        return content_available;
    }

    public void setContent_available(int content_available) {
        this.content_available = content_available;
    }
}