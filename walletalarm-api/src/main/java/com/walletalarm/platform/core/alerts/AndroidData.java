package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AndroidData implements Data {
    //    {
//        "content_available": true,
//            "priority": "high",
//            "to": "dhfeJAdtgiE:APA91bGkMyRSQKk6ThgQYitAm664n0fQUisq_rC7_k3GjuV7PVfgPMnQgwJMyIaCBddOMFEiMr88MOQywqBAZGsqg12C2thJobjGvUnaVOAHOk5dHgZ37_PZImTczbPKtEe7tciBHYya",
//            "data": {
//        "title": "Weekly Notifications",
//                "icon": "alert",
//                "color": "#1a9a9f",
//                "ids": "51",
//                "idType": "tx",
//                "notificationId": "10",
//                "body": "8 new ICO added. Name 1, Name 2, Name 3..."
//          }
//    }
    private String title;
    private String icon;
    private String color;
    private String notificationId;
    private String ids;
    private IdType idType;
    private String body;
    private AndroidAction[] actions;

    public AndroidData() {
        this.icon = "ic_alert";
        this.color = "#222222";
    }

    public AndroidData(String title, String notificationId, String ids, IdType idType, String body) {
        this.title = title;
        this.notificationId = notificationId;
        this.ids = ids;
        this.idType = idType;
        this.body = body;
        this.icon = "ic_alert";
        this.color = "#0931F7";
        initializeActions();
    }

    private void initializeActions() {
        actions = new AndroidAction[2];
        actions[0] = new AndroidAction("Mark As Read", null, "read", true, false, null);
        actions[1] = new AndroidAction("Delete", null, "delete", true, false, null);
    }


    @JsonProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @JsonProperty
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JsonProperty
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    @JsonProperty
    public AndroidAction[] getActions() {
        return actions;
    }

    public void setActions(AndroidAction[] actions) {
        this.actions = actions;
    }
}