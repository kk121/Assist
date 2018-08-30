package com.krishna.assist;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.krishna.assist.data.pojo.Command;

import java.util.Map;

public class Assist {

    public static boolean isAssistNotification(Map<String, String> notificationData) {
        return notificationData != null && notificationData.containsKey("command");
    }

    public static void handleNotification(Context context, Map<String, String> notificationData, String to) {
        if (!isAssistNotification(notificationData)) return;

        Context ctx = context.getApplicationContext();
        String commandStr = notificationData.get("command");
        if (commandStr != null) {
            //parse args
            String argsStr = notificationData.get("args");
            String args[] = null;
            if (!TextUtils.isEmpty(argsStr)) {
                args = argsStr.trim().split(",");
            }
            //parse flags
            String flagsStr = notificationData.get("flags");
            String flags[] = null;
            if (!TextUtils.isEmpty(flagsStr)) {
                flags = flagsStr.trim().split(",");
            }
            //create Command obj
            Command command = new Command();
            command.setCommand(commandStr);
            command.setArgs(args);
            command.setFlags(flags);

            scheduleAssistJob(ctx, command, to);
        }
    }

    private static void scheduleAssistJob(Context context, Command command, String to) {
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        firebaseJobDispatcher.cancel(AssistJobService.TAG);

        Bundle bundle = new Bundle();
        bundle.putSerializable("command", command);
        bundle.putString("to", to);

        Job job = firebaseJobDispatcher.newJobBuilder()
                .setService(AssistJobService.class)
                .setTag(AssistJobService.TAG)
                .setReplaceCurrent(true)
                .setRecurring(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.NOW)
                .setExtras(bundle)
                .build();
        firebaseJobDispatcher.mustSchedule(job);
    }
}
