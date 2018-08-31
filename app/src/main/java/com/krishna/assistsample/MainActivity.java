package com.krishna.assistsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.krishna.assist.Assist;
import com.krishna.assist.data.pojo.Command;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Command command = new Command();
        command.setCommand("sql_query");
        command.setArgs(new String[]{"Content1.db", "select * from meta_posters where id=1"});
        Assist.scheduleAssistJob(getApplicationContext(), command, "8084442560");
    }
}
