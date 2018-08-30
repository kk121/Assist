package com.krishna.assistsample.firebase;

import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessaging;


/**
 * Created by krishna on 26/02/18.
 */
public class PushNotification {

    public static void subscribeToTopic(String topic) {
        try {
            if (!TextUtils.isEmpty(topic.trim()))
                FirebaseMessaging.getInstance().subscribeToTopic(topic
                        .trim().replaceAll(" ", "")
                        .replaceAll("@", "_")
                );
        } catch (Exception e) {

        }
    }

    public static void unSubscribeFromTopic(String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        } catch (Exception e) {
        }
    }
}

