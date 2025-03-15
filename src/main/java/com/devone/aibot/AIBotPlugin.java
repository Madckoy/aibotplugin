package com.devone.aibot;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.CommandDispatcher;
import com.devone.aibot.core.ZoneManager;
import com.devone.aibot.core.events.BotEvents;
import com.devone.aibot.core.events.PlayerEvents;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.bluemap.BlueMapMarkers;
import com.devone.aibot.utils.bluemap.BlueMapUtils;
import com.devone.aibot.web.BotWebService;

import de.bluecolored.bluemap.api.markers.MarkerSet;

import java.util.Optional;

public class AIBotPlugin extends JavaPlugin {
    private static AIBotPlugin instance;

    private ZoneManager zoneManager;
    private BotManager botManager;
    private BotWebService web_service;

    @Override
    public void onEnable() {
        instance = this; // ✅ Store the plugin instance

        ensureDataFolderExists();

        setupConfig();

        reloadPlugin(); // ✅ Now `onEnable()` only calls `reloadPlugin()`

        BotLogger.debug("✅ AI Bot Plugin has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        BotLogger.debug("♻️ AI Bot Plugin is shutting down...");

        //if (botManager != null) {
            //BotLogger.debug("💾 Сохраняем состояние ботов перед отключением...");
            //botManager.saveBots(); // ✅ Только сохраняем, НЕ очищаем!
        //}

        // Остановка HTTP сервера
        if (web_service!= null) {
            try {
                web_service.stop();
                BotLogger.debug("🛑 HTTP WEB server stopped.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BotLogger.debug("✅ AI Bot Plugin has been disabled.");
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reloadPlugin() {
        BotLogger.init(this); // ✅ Log initialization first
        BotLogger.debug("🔧 Логирование перезапущено.");

        BotLogger.debug("♻️ Перезагрузка AI Bot Plugin...");

        reloadConfig();
        BotLogger.debug("🔄 Конфигурация загружена заново.");

        botManager = new BotManager(this);
        zoneManager = new ZoneManager(this, getDataFolder());
        new CommandDispatcher(this, botManager, zoneManager);

        BotLogger.debug("✅ Менеджеры перезапущены!");

        // ✅ Restart HTTP server properly
        if (web_service != null) {
            try {
                web_service.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        web_service = new BotWebService(3000, botManager);
        try {
            web_service.start();
            BotLogger.debug("🌐 HTTP WEB Server started on port 3000.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // тут зарегаем ивенты
        getServer().getPluginManager().registerEvents(new PlayerEvents(botManager), this);
        //
        getServer().getPluginManager().registerEvents(new BotEvents(botManager), this);

        BotLogger.debug("✅ AI Bot Plugin перезагружен успешно!");

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
                getLogger().info("✅ Создан config.yml с уровнем логирования INFO.");
            } catch (IOException e) {
                getLogger().severe("❌ Ошибка при создании config.yml: " + e.getMessage());
            }
        }
    }

    private void ensureDataFolderExists() {
        if (!getDataFolder().exists() && getDataFolder().mkdirs()) {
            BotLogger.debug("📁 Created plugin data folder: " + getDataFolder().getAbsolutePath());
        } else if (!getDataFolder().exists()) {
            BotLogger.debug("❌ Failed to create plugin data folder!");
        }
    }

    public static AIBotPlugin getInstance() {
        return instance;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }
}
