package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IOSData implements Data {
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
    private String notificationId;
    private String ids;
    private IdType idType;

    public IOSData() {
    }

    public IOSData(String notificationId, String ids, IdType idType) {
        this.notificationId = notificationId;
        this.ids = ids;
        this.idType = idType;
    }

    @JsonProperty
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @JsonProperty
    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @JsonProperty
    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }
}