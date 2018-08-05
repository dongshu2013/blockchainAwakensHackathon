package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationCounts {
    private int transactionCount;

    public NotificationCounts() {
    }

    public NotificationCounts(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    @JsonProperty
    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}