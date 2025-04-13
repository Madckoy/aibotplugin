package com.devone.bot.core.logic.task.params;

public class BotTaskParams implements IBotTaskParams {
    public boolean isEnabled;
    public boolean isLogged;

    public BotTaskParams() {
        this.isEnabled = true;
        this.isLogged = true;
    }
}
