package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.walletalarm.platform.core.OS;
import com.walletalarm.platform.core.TransactionStatus;

public class AddressActivityAlert implements SingleAlert, Data {
    private static final String ALERT_TITLE = "Wallet Activity Alert";
    private static final String MESSAGE_BODY_FORMAT = "%s (%s) %s your wallet - %s. Click for more information";
    private int userId;
    private String notificationCode;
    private OS os;
    private String name;
    private String symbol;
    private int transactionId;
    private int notificationId;
    private String addressName;
    private String direction;
    private TransactionStatus status;

    public AddressActivityAlert() {
    }

    public AddressActivityAlert(int userId, String notificationCode, OS os, String name, String symbol,
                                int transactionId, String addressName, String direction, TransactionStatus status) {
        this.userId = userId;
        this.notificationCode = notificationCode;
        this.os = os;
        this.name = name;
        this.symbol = symbol;
        this.transactionId = transactionId;
        this.addressName = addressName;
        this.direction = direction;
        this.status = status;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonProperty
    public String getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(String notificationCode) {
        this.notificationCode = notificationCode;
    }

    @Override
    public String getBody() {
        return String.format(MESSAGE_BODY_FORMAT, name, symbol, direction, addressName);
    }

    @Override
    public String getTitle() {
        return ALERT_TITLE;
    }

    @Override
    public String getNotificationId() {
        return Integer.toString(notificationId);
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public String getIds() {
        return Integer.toString(transactionId);
    }

    @Override
    public IdType getIdType() {
        return IdType.TX;
    }

    @Override
    public String getTo() {
        return getNotificationCode();
    }

    @JsonProperty
    @Override
    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty
    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    @JsonProperty
    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @JsonProperty
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}