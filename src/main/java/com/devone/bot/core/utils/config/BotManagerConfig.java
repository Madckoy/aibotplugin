package com.devone.bot.core.utils.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.devone.bot.core.utils.BotUtils;

public class BotManagerConfig {

    public static class BotEntry {
        public String name;
        public UUID uuid;
    }

    public static class Data {
        public List<BotEntry> bots = new ArrayList<>();
    }


    private final File file;
    private Data data;

    public BotManagerConfig(File file) {
        this.file = file;
        loadOrCreate();
    }

    public Data loadOrCreate() {
        if (file.exists()) {
            this.data = BotUtils.readJson(file, Data.class);
        } else {
            this.data = new Data();
            save();
        }
        return this.data;
    }

    public Data get() {
        return data;
    }

    public void save() {
        BotUtils.writeJson(file, data);
    }
}
