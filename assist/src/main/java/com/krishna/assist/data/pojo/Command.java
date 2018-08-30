package com.krishna.assist.data.pojo;

import java.io.Serializable;

public class Command implements Serializable {
    private String command;
    private String[] args;
    private String[] flags;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] getFlags() {
        return flags;
    }

    public void setFlags(String[] flags) {
        this.flags = flags;
    }
}
