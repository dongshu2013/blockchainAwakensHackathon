package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplaceDevice {
    private String oldDeviceId;
    private String newDeviceId;

    public ReplaceDevice(String oldDeviceId, String newDeviceId) {
        this.oldDeviceId = oldDeviceId;
        this.newDeviceId = newDeviceId;
    }

    public ReplaceDevice() {
    }

    @JsonProperty
    public String getOldDeviceId() {
        return oldDeviceId;
    }

    public void setOldDeviceId(String oldDeviceId) {
        this.oldDeviceId = oldDeviceId;
    }

    @JsonProperty
    public String getNewDeviceId() {
        return newDeviceId;
    }

    public void setNewDeviceId(String newDeviceId) {
        this.newDeviceId = newDeviceId;
    }
}
