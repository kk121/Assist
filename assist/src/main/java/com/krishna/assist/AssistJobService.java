package com.krishna.assist;

import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.krishna.assist.data.pojo.Command;
import com.krishna.assist.utils.CommandInterpreter;

public class AssistJobService extends JobService {
    public static final String TAG = "AssistJobService";

    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.d(TAG, "onStartJob: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Bundle bundle = job.getExtras();
                if (bundle != null) {
                    String command = bundle.getString(Assist.ARG_COMMAND);
                    String args[] = bundle.getStringArray(Assist.ARG_ARGS);
                    String flags[] = bundle.getStringArray(Assist.ARG_FLAGS);
                    String to = bundle.getString(Assist.ARG_TO);
                    if (!TextUtils.isEmpty(command)) {
                        //interpret command
                        Command commandObj = new Command(command, args, flags);
                        try {
                            CommandInterpreter.interpret(AssistJobService.this, commandObj, to);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "failed to run command: " + command + ", " + e.getMessage());
                        }
                    } else {
                        Log.d(TAG, "onStartJob: command is null");
                    }
                }
                jobFinished(job, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
