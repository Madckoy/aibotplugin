package com.devone.bot.core.events;


import org.bukkit.event.Listener;

import com.devone.bot.core.BotManager;


public class BotEvents implements Listener {

    private final BotManager botManager;

    public BotEvents(BotManager botManager) {
        this.botManager = botManager;
    }

}
