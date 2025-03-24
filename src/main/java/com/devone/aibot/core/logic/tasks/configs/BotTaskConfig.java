package com.devone.aibot.core.logic.tasks.configs;

import com.devone.aibot.utils.BotConstants;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import com.devone.aibot.utils.BotLogger;


public class BotTaskConfig {
    
    protected File file;
    protected FileConfiguration config;
    protected String fileName=null;


    public BotTaskConfig(String f_name) {

        fileName = f_name;

        File configFolder = new File(BotConstants.PLUGIN_PATH_CONFIGS_TASKS);
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        if(fileName!=null) {
            this.file = new File(configFolder, fileName);
            this.config = YamlConfiguration.loadConfiguration(file);
            
            if (!file.exists()) {
                generateDefaultConfig();
            }
        }
    }

    public void generateDefaultConfig() {
        config.set("enabled", true);
        config.set("logging", true);
        config.set("priority", 1);
        save();
        BotLogger.debug(isLogging(),"✅ Создан новый конфигурационный файл: " + file.getName());
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public boolean isLogging() {
        return config.getBoolean("logging", true);
    }

    public FileConfiguration getConfig() {
        return config;
    }


    public int getPriority() {
        return config.getInt("priority", 1);
    }

    public void save() {
        try {
            config.save(file);
            BotLogger.debug(this.isLogging(),"✅ Configuration has been saved: " + file.getName());
        } catch (IOException e) {
            BotLogger.error(this.isLogging(),"❌ Error saving configuration for: " + file.getName());
        }
    }
}
