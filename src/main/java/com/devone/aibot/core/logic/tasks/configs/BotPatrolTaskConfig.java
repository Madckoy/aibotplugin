package com.devone.aibot.core.logic.tasks.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotConstants;

public class BotPatrolTaskConfig {
    private final File file;
    private final FileConfiguration config;

    public BotPatrolTaskConfig() {
        File configFolder = new File(BotConstants.PLUGIN_PATH_CONFIGS_TASKS);
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        
        this.file = new File(configFolder, "BotPatrolTask.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        
        if (!file.exists()) {
            generateDefaultConfig();
        }
    }

    private void generateDefaultConfig() {
        config.set("enabled", true);
        config.set("patrol_radius", 100);
        config.set("patrol_points", "");
        save();
        BotLogger.info("✅ Создан новый конфигурационный файл: " + file.getName());
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public int getPatrolRadius() {
        return config.getInt("patrol_radius", 100);
    }

    public String getPatrolPoints() {
        return config.getString("patrol_points", "");
    }

    public void save() {
        try {
            config.save(file);
            BotLogger.info("✅ Конфигурация сохранена: " + file.getName());
        } catch (IOException e) {
            BotLogger.error("❌ Ошибка сохранения конфига для: " + file.getName());
        }
    }
}
