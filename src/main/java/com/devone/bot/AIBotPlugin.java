package com.devone.bot;

import java.util.Set;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.bot.config.AIBotPluginConfig;
import com.devone.bot.config.AIBotPluginConfigManager;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.command.BotCommandsDispatcher;
import com.devone.bot.core.listener.BotListener;
import com.devone.bot.core.listener.PlayerListener;
import com.devone.bot.core.math.BotMathMaxFunction;
import com.devone.bot.core.zone.BotZoneManager;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.server.BotResourceExtractor;
import com.devone.bot.utils.server.ServerUtils;
import com.devone.bot.web.BotWebService;
import com.googlecode.aviator.AviatorEvaluator;

public class AIBotPlugin extends JavaPlugin {
    private static AIBotPlugin instance;

    private BotZoneManager zoneManager;
    private BotManager botManager;
    private BotWebService web_service;
    private AIBotPluginConfigManager configManager;

    public AIBotPlugin() {
        
            super();

            AviatorEvaluator.addFunction(new com.googlecode.aviator.runtime.function.math.MathAbsFunction());
            AviatorEvaluator.addFunction(new BotMathMaxFunction());
    }

    @Override
    public void onEnable() {

        instance = this; // ✅ Store the plugin instance

        ensureDataFolderExists();

        copyEssentialResources();

        setupConfig();

        reloadPlugin(); // ✅ Now `onEnable()` only calls `reloadPlugin()`

        BotLogger.info(true, "✅ AI Bot Plugin has been enabled successfully!");

    }

    @Override
    public void onDisable() {
        BotLogger.info(true, "♻️ AI Bot Plugin is shutting down...");

        ServerUtils.onDisable();

        // Остановка HTTP сервера
        if (web_service != null) {
            try {
                web_service.stop();
                BotLogger.info(true, "🛑 HTTP WEB server stopped.");
            } catch (Exception e) {
                BotLogger.info(true, "❌ HTTP WEB server could not be stopped." + e.getMessage());
            }
        }

        BotLogger.info(true, "✅ AI Bot Plugin has been disabled.");
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reloadPlugin() {
        
        BotLogger.init(this, configManager.getConfig()); // ✅ Log initialization first
        
        BotLogger.info(true, "🔧 Логирование перезапущено.");

        BotLogger.info(true, "♻️ Перезагрузка AI Bot Plugin...");

        reloadConfig();

        BotLogger.info(true, "🔄 Конфигурация загружена заново.");

        botManager = new BotManager(this);
        zoneManager = new BotZoneManager(this, getDataFolder());
        
        new BotCommandsDispatcher(this, botManager, zoneManager);

        BotLogger.info(true, "✅ Менеджеры перезапущены!");

        // ✅ Restart HTTP server properly
        if (web_service != null) {
            try {
                web_service.stop();
            } catch (Exception e) {
                BotLogger.info(true, "❌ Ошибка: " + e.getMessage());
            }
        }

        //web_service = new BotWebService(3000, botManager);
        web_service = new BotWebService(this, botManager);

        try {
            web_service.start();
            BotLogger.info(true, "🌐 HTTP WEB Server started on port 3000.");
        } catch (Exception e) {
            BotLogger.info(true, "❌ Ошибка: " + e.getMessage());
        }

        // тут зарегаем ивенты
        getServer().getPluginManager().registerEvents(new PlayerListener(botManager), this);
        //
        getServer().getPluginManager().registerEvents(new BotListener(botManager), this);

        BotLogger.info(true, "✅ AI Bot Plugin перезагружен успешно!");

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
    }

    private void copyEssentialResources() {
        BotResourceExtractor.copyDirectoryFromJar("web", BotConstants.PLUGIN_PATH + "/web", true, Set.of(".html", ".css", ".js", ".png"));
        BotResourceExtractor.copyDirectoryFromJar("patterns", BotConstants.PLUGIN_PATH + "/patterns", false, Set.of(".json", ".yml"));
    }


    private void ensureDataFolderExists() {
        if (!getDataFolder().exists() && getDataFolder().mkdirs()) {
            BotLogger.info(true, "📁 Created plugin data folder: " + getDataFolder().getAbsolutePath());
        } else if (!getDataFolder().exists()) {
            BotLogger.info(true, "❌ Failed to create plugin data folder!");
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
