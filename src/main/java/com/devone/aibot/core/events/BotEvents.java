package com.devone.aibot.core.events;


import com.devone.aibot.core.BotManager;

import org.bukkit.event.Listener;


public class BotEvents implements Listener {

    private final BotManager botManager;

    public BotEvents(BotManager botManager) {
        this.botManager = botManager;
    }

}
