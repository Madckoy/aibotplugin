package com.devone.aibot.core.logic.tasks.configs;

import com.devone.aibot.utils.BotConstants;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import com.devone.aibot.utils.BotLogger;


public class BotTaskBreakBlockConfig {
    private final File file;
    private final FileConfiguration config;

    public BotTaskBreakBlockConfig() {
        File configFolder = new File(BotConstants.PLUGIN_PATH_CONFIGS_TASKS);
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        
        this.file = new File(configFolder, "BotTaskBreakBlock.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        
        if (!file.exists()) {
            generateDefaultConfig();
        }
    }

    private void generateDefaultConfig() {
        config.set("enabled", true);
        config.set("priority", 1);
        save();
        BotLogger.debug("✅ Создан новый конфигурационный файл: " + file.getName());
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public int getPriority() {
        return config.getInt("priority", 1);
    }

    public void save() {
        try {
            config.save(file);
            BotLogger.debug("✅ Конфигурация сохранена: " + file.getName());
        } catch (IOException e) {
            BotLogger.error("❌ Ошибка сохранения конфига для: " + file.getName());
        }
    }
}
