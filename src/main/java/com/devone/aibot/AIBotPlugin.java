package com.devone.aibot;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.BotCmdDispatcher;
import com.devone.aibot.core.BotZoneManager;
import com.devone.aibot.core.events.BotEvents;
import com.devone.aibot.core.events.PlayerEvents;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.web.BotWebService;

public class AIBotPlugin extends JavaPlugin {
    private static AIBotPlugin instance;

    private BotZoneManager zoneManager;
    private BotManager botManager;
    private BotWebService web_service;

    @Override
    public void onEnable() {
        instance = this; // ✅ Store the plugin instance

        ensureDataFolderExists();

        setupConfig();

        reloadPlugin(); // ✅ Now `onEnable()` only calls `reloadPlugin()`

        BotLogger.info("✅ AI Bot Plugin has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        BotLogger.info("♻️ AI Bot Plugin is shutting down...");

        // Остановка HTTP сервера
        if (web_service!= null) {
            try {
                web_service.stop();
                BotLogger.info("🛑 HTTP WEB server stopped.");
            } catch (Exception e) {
                BotLogger.error("❌ HTTP WEB server could not be stopped." + e.getMessage());
            }
        }

        BotLogger.info("✅ AI Bot Plugin has been disabled.");
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reloadPlugin() {
        BotLogger.init(this); // ✅ Log initialization first
        BotLogger.info("🔧 Логирование перезапущено.");

        BotLogger.info("♻️ Перезагрузка AI Bot Plugin...");

        reloadConfig();
        BotLogger.info("🔄 Конфигурация загружена заново.");

        botManager = new BotManager(this);
        zoneManager = new BotZoneManager(this, getDataFolder());
        new BotCmdDispatcher(this, botManager, zoneManager);

        BotLogger.info("✅ Менеджеры перезапущены!");

        // ✅ Restart HTTP server properly
        if (web_service != null) {
            try {
                web_service.stop();
            } catch (Exception e) {
                BotLogger.error("❌ Ошибка: " + e.getMessage());
            }
        }

        web_service = new BotWebService(3000, botManager);
        try {
            web_service.start();
            BotLogger.info("🌐 HTTP WEB Server started on port 3000.");
        } catch (Exception e) {
            BotLogger.error("❌ Ошибка: " + e.getMessage());
        }

        // тут зарегаем ивенты
        getServer().getPluginManager().registerEvents(new PlayerEvents(botManager), this);
        //
        getServer().getPluginManager().registerEvents(new BotEvents(botManager), this);

        BotLogger.info("✅ AI Bot Plugin перезагружен успешно!");

    }

    private void setupConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            getLogger().warning("⚠ Файл config.yml не найден, создаем новый...");

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            FileConfiguration config = new YamlConfiguration();
            config.set("logging.level", "INFO");

            try {
                config.save(configFile);
                BotLogger.info("✅ Создан config.yml с уровнем логирования INFO.");
            } catch (IOException e) {
                BotLogger.error("❌ Ошибка при создании config.yml: " + e.getMessage());
            }
        }
    }

    private void ensureDataFolderExists() {
        if (!getDataFolder().exists() && getDataFolder().mkdirs()) {
            BotLogger.info("📁 Created plugin data folder: " + getDataFolder().getAbsolutePath());
        } else if (!getDataFolder().exists()) {
            BotLogger.error("❌ Failed to create plugin data folder!");
        }
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
