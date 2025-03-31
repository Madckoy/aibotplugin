package com.devone.aibot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.BotCmdDispatcher;
import com.devone.aibot.core.BotZoneManager;
import com.devone.aibot.core.events.BotEvents;
import com.devone.aibot.core.events.PlayerEvents;
import com.devone.aibot.core.math.BotMathMaxFunction;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.ServerUtils;
import com.devone.aibot.web.BotWebService;
import com.googlecode.aviator.AviatorEvaluator;

public class AIBotPlugin extends JavaPlugin {
    private static AIBotPlugin instance;

    private BotZoneManager zoneManager;
    private BotManager botManager;
    private BotWebService web_service;

    public AIBotPlugin() {
        
            super();

            AviatorEvaluator.addFunction(new com.googlecode.aviator.runtime.function.math.MathAbsFunction());
            AviatorEvaluator.addFunction(new BotMathMaxFunction());
    }

    @Override
    public void onEnable() {
        instance = this; // ✅ Store the plugin instance

        ensureDataFolderExists();

        copyEntireResourcesToPluginFolder();

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
        BotLogger.init(this); // ✅ Log initialization first
        BotLogger.info(true, "🔧 Логирование перезапущено.");

        BotLogger.info(true, "♻️ Перезагрузка AI Bot Plugin...");

        reloadConfig();

        BotLogger.info(true, "🔄 Конфигурация загружена заново.");

        botManager = new BotManager(this);
        zoneManager = new BotZoneManager(this, getDataFolder());
        new BotCmdDispatcher(this, botManager, zoneManager);

        BotLogger.info(true, "✅ Менеджеры перезапущены!");

        // ✅ Restart HTTP server properly
        if (web_service != null) {
            try {
                web_service.stop();
            } catch (Exception e) {
                BotLogger.info(true, "❌ Ошибка: " + e.getMessage());
            }
        }

        web_service = new BotWebService(3000, botManager);
        try {
            web_service.start();
            BotLogger.info(true, "🌐 HTTP WEB Server started on port 3000.");
        } catch (Exception e) {
            BotLogger.info(true, "❌ Ошибка: " + e.getMessage());
        }

        // тут зарегаем ивенты
        getServer().getPluginManager().registerEvents(new PlayerEvents(botManager), this);
        //
        getServer().getPluginManager().registerEvents(new BotEvents(botManager), this);

        BotLogger.info(true, "✅ AI Bot Plugin перезагружен успешно!");

    }

    private void setupConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
    
        if (!configFile.exists()) {
            getLogger().warning("⚠ Файл config.yml не найден, копируем из ресурсов...");
    
            // Убедимся, что папка существует
            if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
                BotLogger.info(true, "❌ Не удалось создать папку плагина: " + getDataFolder());
                return;
            }
    
            // Копируем config.yml из resources
            try (InputStream in = getResource("config.yml")) {
                if (in == null) {
                    BotLogger.info(true, "❌ config.yml не найден в resources!");
                    return;
                }
                Files.copy(in, configFile.toPath());
                BotLogger.info(true, "✅ Скопирован config.yml из ресурсов.");
            } catch (IOException e) {
                BotLogger.info(true, "❌ Ошибка при копировании config.yml: " + e.getMessage());
            }
        }
    }

    private void copyEntireResourcesToPluginFolder() {

        copyResourcesRecursively("web", BotConstants.PLUGIN_PATH + "/web");
        copyResourcesRecursively("patterns", BotConstants.PLUGIN_PATH + "/patterns");
        copyResourcesRecursively("cfg", BotConstants.PLUGIN_PATH + "/cfg");
        copyResourcesRecursively("logs", BotConstants.PLUGIN_PATH + "/logs");
        copyResourcesRecursively("tmp", BotConstants.PLUGIN_PATH + "/tmp");
    }

    private void copyResourcesRecursively(String resourceSubPath, String targetDirPath) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            URL resourceURL = classLoader.getResource(resourceSubPath.isEmpty() ? "." : resourceSubPath);

            if (resourceURL == null) {
                BotLogger.info(true, "❌ Resource path not found: " + resourceSubPath);
                return;
            }

            if (resourceURL.getProtocol().equals("jar")) {
                String jarPath = resourceURL.getPath().substring(5, resourceURL.getPath().indexOf("!"));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(resourceSubPath) && !entry.isDirectory()) {
                            try (InputStream in = jar.getInputStream(entry)) {
                                String relativePath = name.substring(resourceSubPath.length()).replaceFirst("^/", "");
                                File targetFile = new File(targetDirPath, relativePath);
                                if (!targetFile.exists()) {
                                    targetFile.getParentFile().mkdirs();
                                    Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    BotLogger.info(true, "✅ Copied: " + name + " → " + targetFile.getPath());
                                }
                            }
                        }
                    }
                }
            } else {
                Path sourcePath = Paths.get(resourceURL.toURI());
                Files.walk(sourcePath)
                        .filter(Files::isRegularFile)
                        .forEach(sourceFile -> {
                            try (InputStream in = Files.newInputStream(sourceFile)) {
                                Path relativePath = sourcePath.relativize(sourceFile);
                                File targetFile = new File(targetDirPath, relativePath.toString());
                                targetFile.getParentFile().mkdirs();
                                Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                BotLogger.info(true, "✅ Copied: " + sourceFile + " → " + targetFile.getPath());
                            } catch (IOException e) {
                                BotLogger.info(true, "❌ Failed to copy file: " + e.getMessage());
                            }
                        });
            }

        } catch (Exception e) {
            BotLogger.info(true, "❌ Error during resource copying: " + e.getMessage());
        }
    }

    private void ensureDataFolderExists() {
        if (!getDataFolder().exists() && getDataFolder().mkdirs()) {
            BotLogger.info(true, "📁 Created plugin data folder: " + getDataFolder().getAbsolutePath());
        } else if (!getDataFolder().exists()) {
            BotLogger.info(true, "❌ Failed to create plugin data folder!");
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
