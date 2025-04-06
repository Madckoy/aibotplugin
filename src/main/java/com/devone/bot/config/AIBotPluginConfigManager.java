package com.devone.bot.config;

import java.io.File;

public class AIBotPluginConfigManager {

    private final AIBotBaseJsonConfig<AIBotPluginConfig> baseConfig;

    public AIBotPluginConfigManager(File pluginFolder) {
        File configFile = new File(pluginFolder, "config.json");
        this.baseConfig = new AIBotBaseJsonConfig<>(configFile, AIBotPluginConfig.class);
    }

    public void loadOrCreate() {
        baseConfig.loadOrCreate();
    }

    public void save() {
        baseConfig.save();
    }

    public void reset() {
        baseConfig.deleteAndRegenerate();
    }

    public AIBotPluginConfig getConfig() {
        return baseConfig.get();
    }

    public File getFile() {
        return baseConfig.getFile();
    }
}
