package com.walletalarm.platform.core.alerts;

import com.walletalarm.platform.core.OS;

public interface Alert {
    String getBody();

    String getTitle();

    String getNotificationId();

    String getIds();

    IdType getIdType();

    OS getOs();
}
