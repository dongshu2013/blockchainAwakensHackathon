package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AndroidMessage {
    //    {
//        "content_available": true,
//            "priority": "high",
//            "to": "dhfeJAdtgiE:APA91bGkMyRSQKk6ThgQYitAm664n0fQUisq_rC7_k3GjuV7PVfgPMnQgwJMyIaCBddOMFEiMr88MOQywqBAZGsqg12C2thJobjGvUnaVOAHOk5dHgZ37_PZImTczbPKtEe7tciBHYya",
//            "data": {
//                "title": "Weekly Notifications",
//                "icon": "alert",
//                "color": "#1a9a9f",
//                "ids": "51",
//                "idType": "tx",
//                "notificationId": "10",
//                "body": "8 new ICO added. Name 1, Name 2, Name 3..."
//          }
//    }
    private boolean content_available;
    private String priority;
    private String to;
    private String[] registration_ids;
    private AndroidData data;

    public AndroidMessage() {
        content_available = true;
        priority = "high";
    }

    public AndroidMessage(String to, AndroidData data) {
        this.to = to;
        doContruct(data);
    }

    public AndroidMessage(String[] registration_ids, AndroidData data) {
        this.registration_ids = registration_ids;
        doContruct(data);
    }

    private void doContruct(AndroidData data) {
        this.data = data;
        content_available = true;
        priority = "high";
    }

    @JsonProperty
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @JsonProperty
    public AndroidData getData() {
        return data;
    }

    public void setData(AndroidData data) {
        this.data = data;
    }

    @JsonProperty
    public boolean isContent_available() {
        return content_available;
    }

    public void setContent_available(boolean content_available) {
        this.content_available = content_available;
    }

    @JsonProperty
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @JsonProperty
    public String[] getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(String[] registration_ids) {
        this.registration_ids = registration_ids;
    }
}