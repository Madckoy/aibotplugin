package com.devone.aibot;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.CommandDispatcher;
import com.devone.aibot.core.ZoneManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.bluemap.BlueMapMarkers;
import com.devone.aibot.web.BotStatusServer;

public class AIBotPlugin extends JavaPlugin {
    private static AIBotPlugin instance;

    private ZoneManager zoneManager;
    private BotManager botManager;
    private BotStatusServer status_server;

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
        if (status_server != null) {
            try {
                status_server.stop();
                BotLogger.debug("🛑 HTTP server stopped.");
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
        if (status_server != null) {
            try {
                status_server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        status_server = new BotStatusServer(3000, botManager);
        try {
            status_server.start();
            BotLogger.debug("🌐 HTTP Server started on port 3000.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        BotLogger.debug("✅ AI Bot Plugin перезагружен успешно!");

        //DynmapBotMarkers dynmapBotMarkers = new DynmapBotMarkers(botManager);
        //dynmapBotMarkers.scheduleMarkerUpdate();
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
