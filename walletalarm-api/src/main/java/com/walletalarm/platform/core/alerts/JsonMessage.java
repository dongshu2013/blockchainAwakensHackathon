package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMessage {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String getJsonMessage(SingleAlert singleAlert) throws JsonProcessingException {
        String jsonMessage = null;
        switch (singleAlert.getOs()) {
            case IOS:
                IOSData iosData = new IOSData(singleAlert.getNotificationId(), singleAlert.getIds(), singleAlert.getIdType());
                IOSNotification iosNotification = new IOSNotification(singleAlert.getBody(), singleAlert.getTitle());
                IOSMessage iosMessage = new IOSMessage(iosNotification, iosData, singleAlert.getTo());
                jsonMessage = MAPPER.writeValueAsString(iosMessage);
                break;
            case ANDROID:
                AndroidData androidData = new AndroidData(singleAlert.getTitle(), singleAlert.getNotificationId(), singleAlert.getIds(),
                        singleAlert.getIdType(), singleAlert.getBody());
                AndroidMessage androidMessage = new AndroidMessage(singleAlert.getTo(), androidData);
                jsonMessage = MAPPER.writeValueAsString(androidMessage);
                break;
        }
        return jsonMessage;
    }

    public static String getJsonMessage(BatchAlert batchAlert) throws JsonProcessingException {
        String jsonMessage = null;
        switch (batchAlert.getOs()) {
            case IOS:
                IOSData iosData = new IOSData(batchAlert.getNotificationId(), batchAlert.getIds(), batchAlert.getIdType());
                IOSNotification iosNotification = new IOSNotification(batchAlert.getBody(), batchAlert.getTitle());
                IOSMessage iosMessage = new IOSMessage(iosNotification, iosData, batchAlert.getRegistrationIds());
                jsonMessage = MAPPER.writeValueAsString(iosMessage);
                break;
            case ANDROID:
                AndroidData androidData = new AndroidData(batchAlert.getTitle(), batchAlert.getNotificationId(), batchAlert.getIds(),
                        batchAlert.getIdType(), batchAlert.getBody());
                AndroidMessage androidMessage = new AndroidMessage(batchAlert.getRegistrationIds(), androidData);
                jsonMessage = MAPPER.writeValueAsString(androidMessage);
                break;
        }
        return jsonMessage;
    }
}