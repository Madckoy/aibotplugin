package com.devone.bot;

import java.util.Set;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.bot.core.BotManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.server.BotResourceExtractor;
import com.devone.bot.core.utils.server.BotServerUtils;
import com.devone.bot.core.utils.zone.BotZoneManager;
import com.devone.bot.core.web.BotWebService;
import com.devone.bot.plugin.command.BotCommandsDispatcher;
import com.devone.bot.plugin.config.AIBotPluginConfig;
import com.devone.bot.plugin.config.AIBotPluginConfigManager;
import com.devone.bot.plugin.listener.BotListener;
import com.devone.bot.plugin.listener.PlayerListener;

public class AIBotPlugin extends JavaPlugin {
    private static AIBotPlugin instance;

    private BotZoneManager zoneManager;
    private BotManager botManager;
    private BotWebService web_service;
    private AIBotPluginConfigManager configManager;
    private boolean isLogged = true;

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public AIBotPlugin() {
        
            super();
    }

    @Override
    public void onEnable() {

        instance = this; // ✅ Store the plugin instance

        ensureDataFolderExists();

        copyEssentialResources();

        setupConfig();

        reloadPlugin(); // ✅ Now `onEnable()` only calls `reloadPlugin()`

        BotLogger.debug("✅ AIBotPlugin: onEnable", isLogged(), "AI Bot Plugin has been enabled successfully!");

    }

    @Override
    public void onDisable() {
        BotLogger.debug("♻️ AIBotPlugin: onDisable", isLogged(), "AI Bot Plugin is shutting down...");

        if (botManager != null) {
            botManager.saveBots(); // 💾 сохраняем всех ботов
        }

        BotServerUtils.onDisable();

        // Остановка HTTP сервера
        if (web_service != null) {
            try {
                web_service.stop();
                BotLogger.debug("🛑", isLogged(), "HTTP WEB server stopped");
            } catch (Exception e) {
                BotLogger.debug("❌", isLogged(), "HTTP WEB server could not be stopped" + e.getMessage());
            }
        }

        BotLogger.debug("✅", isLogged(), "AI Bot Plugin has been disabled");
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reloadPlugin() {
        
        BotLogger.init(this, configManager.getConfig()); // ✅ Log initialization first
        
        BotLogger.debug("🔧", isLogged(), "Логирование перезапущено");

        BotLogger.debug("♻️", isLogged(), "Перезагрузка AI Bot Plugin");

        reloadConfig();

        BotLogger.debug("🔄", isLogged(), "Конфигурация загружена заново.");

        botManager = new BotManager(this);
        zoneManager = new BotZoneManager(this, getDataFolder());
        
        new BotCommandsDispatcher(this, botManager, zoneManager);

        BotLogger.debug("✅", isLogged(), "Менеджеры перезапущены!");

        // ✅ Restart HTTP server properly
        if (web_service != null) {
            try {
                web_service.stop();
            } catch (Exception e) {
                BotLogger.debug("❌", isLogged(), "Ошибка: " + e.getMessage());
            }
        }

        //web_service = new BotWebService(3000, botManager);
        web_service = new BotWebService(this, botManager);

        try {
            web_service.start();
            BotLogger.debug("🌐", isLogged(), "HTTP WEB Server started");
        } catch (Exception e) {
            BotLogger.debug("❌", isLogged(), "Ошибка: " + e.getMessage());
        }

        // тут зарегаем ивенты
        getServer().getPluginManager().registerEvents(new PlayerListener(botManager), this);
        //
        getServer().getPluginManager().registerEvents(new BotListener(botManager), this);

        BotLogger.debug("✅", isLogged(), "AI Bot Plugin перезагружен успешно!");

    }

    private void setupConfig() {

        this.configManager = new AIBotPluginConfigManager(getDataFolder());
        this.configManager.loadOrCreate();
    
        AIBotPluginConfig config = configManager.getConfig();

        getLogger().info("Logging enabled: " + config.logging.enable);
        getLogger().info("Logging level: "   + config.logging.level);
        getLogger().info("Web host: " + config.server.web_host);
        getLogger().info("Web port: " + config.server.web_port);
        getLogger().info("map host: " + config.server.map_host);
        getLogger().info("map port: " + config.server.map_port);

        setLogged(config.logging.enable);
    }

    private void copyEssentialResources() {
        BotResourceExtractor.copyDirectoryFromJar("web", BotConstants.PLUGIN_PATH + "/web", true , Set.of(".html", ".css", ".js", ".png"));
        BotResourceExtractor.copyDirectoryFromJar("patterns", BotConstants.PLUGIN_PATH + "/patterns", false, Set.of(".json"));
        BotResourceExtractor.copyDirectoryFromJar("config", BotConstants.PLUGIN_PATH_CONFIGS, false, Set.of(".me"));
        BotResourceExtractor.copyDirectoryFromJar("tmp", BotConstants.PLUGIN_PATH_TMP, false, Set.of(".me"));
    }


    private void ensureDataFolderExists() {
        if (!getDataFolder().exists() && getDataFolder().mkdirs()) {
            BotLogger.debug("📁 ", isLogged(), "Created plugin data folder: " + getDataFolder().getAbsolutePath());
        } else if (!getDataFolder().exists()) {
            BotLogger.debug("❌ ", isLogged(), "Failed to create plugin data folder!");
        }
    }

    public AIBotPluginConfigManager getConfigManager(){
        return this.configManager;
    }   
    
    public static AIBotPlugin getInstance() {
        return instance;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public BotZoneManager getZoneManager() {
        return zoneManager;
    }
}
