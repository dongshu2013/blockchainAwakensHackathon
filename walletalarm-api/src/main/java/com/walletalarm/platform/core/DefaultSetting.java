package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class DefaultSetting {
    private int defaultSettingId;
    private SettingType type;
    private SettingCategory category;
    private String text;
    private boolean value;
    private int sortOrder;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;

    public DefaultSetting() {
    }

    public DefaultSetting(int defaultSettingId, SettingType type, SettingCategory category, String text, boolean value,
                          Timestamp createdTime, Timestamp modifiedTime, boolean isActive) {
        this.defaultSettingId = defaultSettingId;
        this.type = type;
        this.category = category;
        this.text = text;
        this.value = value;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.isActive = isActive;
    }

    @JsonProperty
    public int getDefaultSettingId() {
        return defaultSettingId;
    }

    public void setDefaultSettingId(int defaultSettingId) {
        this.defaultSettingId = defaultSettingId;
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
    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @JsonProperty
    public SettingCategory getCategory() {
        return category;
    }

    public void setCategory(SettingCategory category) {
        this.category = category;
    }
}
