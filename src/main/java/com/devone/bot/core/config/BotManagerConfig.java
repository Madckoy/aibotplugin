package com.devone.bot.core.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.devone.bot.config.AIBotBaseJsonConfig;
import com.devone.bot.utils.BotCoordinate3D;

public class BotManagerConfig extends AIBotBaseJsonConfig<BotManagerConfig.Data> {

    public BotManagerConfig(File file) {
        super(file, Data.class);
    }

    public static class Data {
        public Map<String, BotEntry> bots = new HashMap<>();
    }

    public static class BotEntry {
        public String uuid;
        public BotCoordinate3D position;

        public BotEntry() {}

        public BotEntry(String uuid, BotCoordinate3D position) {
            this.uuid = uuid;
            this.position = position;
        }
    }
}
