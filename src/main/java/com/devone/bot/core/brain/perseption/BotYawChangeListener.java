package com.devone.bot.core.brain.perseption;

import com.devone.bot.core.Bot;

public interface BotYawChangeListener {
    void onYawChanged(Bot bot, float newYaw);
}
