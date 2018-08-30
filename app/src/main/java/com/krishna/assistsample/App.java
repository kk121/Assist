package com.krishna.assistsample;

import android.app.Application;

import com.krishna.assistsample.firebase.PushNotification;

public class App extends Application {
    private static final String FCM_TOPIC = "/topics/assist";

    @Override
    public void onCreate() {
        super.onCreate();
        PushNotification.subscribeToTopic(FCM_TOPIC);
    }
}
