package com.krishna.assist.api;

import com.google.gson.annotations.SerializedName;

public class NotificationData {
    @SerializedName("from")
    private String from;
    @SerializedName("command")
    private String command;
    @SerializedName("args")
    private String args;
    @SerializedName("flags")
    private String flags;
    @SerializedName("result")
    private String result;

    public NotificationData(String from, String command, String args, String flags, String result) {
        this.from = from;
        this.command = command;
        this.args = args;
        this.flags = flags;
        this.result = result;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }
}
