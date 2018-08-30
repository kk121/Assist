package com.krishna.assist.api;

import com.google.gson.annotations.SerializedName;

public class RequestNotificaton {
    @SerializedName("to")
    private String topic;

    @SerializedName("data")
    private NotificationData notificationData;

    public RequestNotificaton(String topic, NotificationData notificationData) {
        this.topic = topic;
        this.notificationData = notificationData;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }
}
