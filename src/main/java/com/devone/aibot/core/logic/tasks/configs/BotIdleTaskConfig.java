package com.devone.aibot.core.logic.tasks.configs;

import com.devone.aibot.utils.Constants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import com.devone.aibot.utils.BotLogger;


public class BotIdleTaskConfig {
    private final File file;
    private final FileConfiguration config;

    public BotIdleTaskConfig() {
        File configFolder = new File(Constants.PLUGIN_PATH_CONFIGS_TASKS);
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        
        this.file = new File(configFolder, "BotIdleTask.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        
        if (!file.exists()) {
            generateDefaultConfig();
        }
    }

    private void generateDefaultConfig() {
        config.set("enabled", true);
        config.set("priority", 1);
        save();
        BotLogger.debug("[BotIdleTaskConfig] Создан новый конфигурационный файл: " + file.getName());
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
            BotLogger.debug("[BotIdleTaskConfig] Конфигурация сохранена: " + file.getName());
        } catch (IOException e) {
            BotLogger.debug("[BotIdleTaskConfig] Ошибка сохранения конфига для: " + file.getName());
        }
    }
}
