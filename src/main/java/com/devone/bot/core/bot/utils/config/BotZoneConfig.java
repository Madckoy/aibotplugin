package com.devone.bot.core.bot.utils.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.devone.bot.plugin.config.AIBotBaseJsonConfig;

public class BotZoneConfig extends AIBotBaseJsonConfig<BotZoneConfig.Data> {

    public BotZoneConfig(File file) {
        super(file, Data.class);
    }

    public static class Data {
        public Map<String, ZoneEntry> zones = new HashMap<>();
    }

    public static class ZoneEntry {
        public double x;
        public double y;
        public double z;
        public int radius;

        public ZoneEntry() {}

        public ZoneEntry(double x, double y, double z, int radius) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
        }
    }
}
