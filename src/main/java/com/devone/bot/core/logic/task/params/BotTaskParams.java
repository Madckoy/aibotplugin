package com.devone.bot.core.logic.task.params;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.logger.BotLogger;

public class BotTaskParams implements IBotTaskParams, IBotTaskParamsConfigurable {
    private String  icon       = "☑️";
    private boolean isEnabled = true;
    private boolean isLogging = true;
    private String  objective = "Do something abstract";
    
    protected String fileName = null;
    protected File file;
    protected FileConfiguration config;

    public BotTaskParams(String p_class_name) {

        fileName = p_class_name + ".yml";

        File configFolder = new File(BotConstants.PLUGIN_PATH_CONFIGS_TASKS);

        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        if(fileName!=null || !fileName.equals("")) {
            loadFile(configFolder, fileName);
            if (!file.exists()) {
                saveDefaultFile();
            }
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }
    
    public void setEnable(boolean enbl) {
        isEnabled = enbl;
    }

    public boolean isLogging() {
        return isEnabled;
    }

    public void setIsLogging(boolean lgn) {
        isLogging = lgn;
    }

    public void setObjective(String obj){
        objective = obj;
    }

    public String getIcon() {
        return icon;
    } 
    public void setIcon(String icn) {
        icon = icn;
    }

    public String getObjective(){
        return objective;
    }

    @Override
    public Object saveDefaultFile() {
        try {
            config.save(file);
            BotLogger.info("✅", true,"Configuration has been saved: " + file.getName());
        } catch (IOException e) {
            BotLogger.info("❌", true,"Error saving configuration for: " + file.getName());
        }
        return this;
    }

    @Override
    public Object loadFile(File configFolder, String fileName) {
        this.file = new File(configFolder, fileName);
        this.config = YamlConfiguration.loadConfiguration(file);
        BotLogger.info("🟢", true,"Loading task configuration: " + file.getName());
        return this;
    }

    @Override
    public Object setDefaults() {
        config.set("icon", this.icon);
        config.set("objective", this.objective);
        config.set("enable", this.isEnabled);
        config.set("logging", this.isLogging);

        saveDefaultFile();

        return this;
    }
    @Override
    public Object copyFrom(IBotTaskParams source) {
        icon      = ((BotTaskParams)source).getIcon();
        isEnabled = ((BotTaskParams)source).isEnabled();
        isLogging = ((BotTaskParams)source).isLogging();
        objective = ((BotTaskParams)source).getObjective();
        return this;
    }
    public FileConfiguration getConfig() {
        return config;
    }

}
