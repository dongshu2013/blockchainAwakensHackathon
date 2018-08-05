package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Setting {
    private int settingId;
    private int userId;
    private int defaultSettingId;
    private SettingType type;
    private SettingCategory category;
    private String text;
    private boolean value;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;

    public Setting() {
    }

    public Setting(int settingId, int defaultSettingId, int userId, SettingType type, SettingCategory category,
                   String text, boolean value) {
        this.settingId = settingId;
        this.defaultSettingId = defaultSettingId;
        this.userId = userId;
        this.type = type;
        this.category = category;
        this.text = text;
        this.value = value;
    }

    @JsonProperty
    public int getSettingId() {
        return settingId;
    }

    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

    @JsonProperty
    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
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
    public SettingType getType() {
        return type;
    }

    public void setType(SettingType type) {
        this.type = type;
    }

    @JsonProperty
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty
    public int getDefaultSettingId() {
        return defaultSettingId;
    }

    public void setDefaultSettingId(int defaultSettingId) {
        this.defaultSettingId = defaultSettingId;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public SettingCategory getCategory() {
        return category;
    }

    public void setCategory(SettingCategory category) {
        this.category = category;
    }
}
