package com.devone.aibot.core.config;

import com.devone.aibot.config.AIBotBaseJsonConfig;
import com.devone.aibot.utils.BotCoordinate3D;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
