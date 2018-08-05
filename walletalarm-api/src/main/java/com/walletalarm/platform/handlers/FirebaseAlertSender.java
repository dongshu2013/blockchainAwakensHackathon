package com.walletalarm.platform.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseAlertSender {
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private final String fcmToken;

    public FirebaseAlertSender(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void sendMessage(String message) throws IOException {
        URL url = new URL(FCM_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "key=" + fcmToken);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(message.getBytes());
        httpURLConnection.getResponseCode();
        httpURLConnection.getResponseMessage();
    }
}
