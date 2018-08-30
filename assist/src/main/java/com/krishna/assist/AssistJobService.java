package com.krishna.assist;

import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.krishna.assist.data.pojo.Command;
import com.krishna.assist.utils.CommandInterpreter;

public class AssistJobService extends JobService {
    public static final String TAG = "AssistJobService";

    @Override
    public boolean onStartJob(final JobParameters job) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Bundle bundle = job.getExtras();
                if (bundle != null) {
                    Command command = (Command) bundle.getSerializable("command");
                    String to = bundle.getString("to");
                    if (command != null) {
                        //interpret command
                        try {
                            CommandInterpreter.interpret(AssistJobService.this, command, to);
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
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
