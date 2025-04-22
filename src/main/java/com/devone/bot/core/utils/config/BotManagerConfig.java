package com.devone.bot.core.utils.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.BotBrain;
import com.devone.bot.core.bot.brain.logic.navigator.BotNavigator;
import com.devone.bot.core.bot.state.BotState;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.plugin.config.AIBotBaseJsonConfig;

public class BotManagerConfig extends AIBotBaseJsonConfig<BotManagerConfig.Data> {

    public BotManagerConfig(File file) {
        super(file, Data.class);
    }

    public static class Data {
        public Map<String, BotEntry> bots = new HashMap<>();
    }

    public static class BotEntry {
        public boolean enabled = true;
        public String id;
        public String uuid;
        public BotLocation position;
        public boolean allowPickup;
        public BotBrain brain;
        public BotState state;
        public BotNavigator navigation;

        public BotEntry() {}

        public BotEntry(Bot bot) {
            this.id = bot.getId();
            this.enabled = bot.isEnabled();
            this.uuid = bot.getUuid().toString();
            this.position = bot.getNavigation().getLocation();
            this.allowPickup = bot.isAllowPickupItems();
            this.brain = bot.getBrain();
            this.state = bot.getState();
            this.navigation = bot.getNavigation();
        }
    }
}
