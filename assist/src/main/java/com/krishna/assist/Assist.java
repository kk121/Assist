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
    private static final String TAG = "Assist";
    public static final String ARG_COMMAND = "command";
    public static final String ARG_ARGS = "args";
    public static final String ARG_FLAGS = "flags";
    public static final String ARG_TO = "to";

    public static boolean isAssistNotification(Map<String, String> notificationData) {
        return notificationData != null && notificationData.containsKey("command");
    }

    public static void handleNotification(Context context, Map<String, String> notificationData, String to) {
        if (!isAssistNotification(notificationData)) return;

        Context ctx = context.getApplicationContext();
        String commandStr = notificationData.get(ARG_COMMAND);
        if (commandStr != null) {
            //parse args
            String argsStr = notificationData.get(ARG_ARGS);
            String args[] = null;
            if (!TextUtils.isEmpty(argsStr)) {
                args = argsStr.trim().split(",");
            }
            //parse flags
            String flagsStr = notificationData.get(ARG_FLAGS);
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

    public static void scheduleAssistJob(Context context, Command command, String to) {
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        firebaseJobDispatcher.cancelAll();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_COMMAND, command.getCommand());
        bundle.putStringArray(ARG_ARGS, command.getArgs());
        bundle.putStringArray(ARG_FLAGS, command.getFlags());
        bundle.putString(ARG_TO, to);

        Job job = firebaseJobDispatcher.newJobBuilder()
                .setService(AssistJobService.class)
                .setTag(AssistJobService.TAG)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.NOW)
                .setExtras(bundle)
                .build();
        firebaseJobDispatcher.mustSchedule(job);
    }
}
