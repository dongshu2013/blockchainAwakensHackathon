package com.walletalarm.platform.core.alerts;

public interface BatchAlert extends Alert {
    String[] getRegistrationIds();
}
