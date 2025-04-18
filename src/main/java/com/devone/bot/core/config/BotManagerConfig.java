package com.devone.bot.core.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.devone.bot.config.AIBotBaseJsonConfig;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.utils.blocks.BotLocation;

public class BotManagerConfig extends AIBotBaseJsonConfig<BotManagerConfig.Data> {

    public BotManagerConfig(File file) {
        super(file, Data.class);
    }

    public static class Data {
        public Map<String, BotEntry> bots = new HashMap<>();
    }

    public static class BotEntry {
        public boolean enabled = true;
        public String uuid;
        public BotLocation position;
        public boolean allowPickup;

        public BotEntry() {}

        public BotEntry(Bot bot) {
            this.enabled = bot.isEnabled();
            this.uuid = bot.getUuid().toString();
            this.position = bot.getBrain().getCurrentLocation();
            this.allowPickup = bot.isAllowPickupItems();
        }
    }
}
