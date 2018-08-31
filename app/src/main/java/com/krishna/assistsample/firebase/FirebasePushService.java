package com.krishna.assistsample.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.krishna.assistsample.R;

import java.util.Map;

public class FirebasePushService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "push";
    private static int NOTIFICATION_ID = 1000;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> notificationData = remoteMessage.getData();
        if (notificationData != null) {
            String from = notificationData.get("from_");
            String command = notificationData.get("command");
            String args = notificationData.get("args");
            String flags = notificationData.get("flags");
            String result = notificationData.get("result");

            //create notification
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(from)
                    .setContentText(command)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("args: " + args + "\n" +
                                    "flags: " + flags + "\n" +
                                    "result: " + "\n" +
                                    result))
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
                        && notificationManager.getNotificationChannel(CHANNEL_ID) != null) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = null;
                    notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                notificationManager.notify(++NOTIFICATION_ID, notification);
            }
        }
    }
}
