package com.devone.aibot.utils;

import org.bukkit.configuration.file.FileConfiguration;
import com.devone.aibot.AIBotPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class BotLogger {
    private static final Logger logger = Logger.getLogger("AIBotPlugin");
    private static Level logLevel = Level.INFO;
    private static boolean loggingEnabled = true;
    private static FileHandler fileHandler;
    private static final String LOG_FILE_PATH = "plugins/AIBotPlugin/logs/console.log";

    public static void init(AIBotPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        loggingEnabled = config.getBoolean("logging.enable", true);
        String levelStr = config.getString("logging.level", "INFO").toUpperCase();

        // ✅ Создаём каталог, если нужно
        try {
            File logFile = new File(LOG_FILE_PATH);
            logFile.getParentFile().mkdirs();

            fileHandler = new FileHandler(LOG_FILE_PATH, 10 * 1024 * 1024, 5, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("❌ Ошибка инициализации логгера: " + e.getMessage());
            fileHandler = null;
        }

        // Уровень логирования
        try {
            logLevel = Level.parse(levelStr);
        } catch (IllegalArgumentException e) {
            logLevel = Level.SEVERE;
            error(true, "❌ Некорректный уровень логирования в config.yml, используется SEVERE.");
        }

        logger.setLevel(logLevel);
        if (fileHandler != null) {
            fileHandler.setLevel(logLevel);
        }

        logger.setUseParentHandlers(false);
        info(true, "🔧 Установлен уровень логирования: " + logLevel.getName());
    }

    public static void debug(boolean enabled, String message) {
        if (enabled && loggingEnabled && logLevel.intValue() == Level.FINE.intValue()) {
            logger.fine("🟡 " + message);
        }
    }

    public static void info(boolean enabled, String message) {
        if (enabled && loggingEnabled && logLevel.intValue() == Level.INFO.intValue()) {
            logger.info("ℹ️ " + message);
        }
    }

    public static void warn(boolean enabled, String message) {
        if (enabled && loggingEnabled && logLevel.intValue() == Level.WARNING.intValue()) {
            logger.warning("⚠️ " + message);
        }
    }

    public static void error(boolean enabled, String message) {
        if (enabled && loggingEnabled && logLevel.intValue() == Level.SEVERE.intValue()) {
            logger.severe("🚨 " + message);
        }
    }

    public static void trace(boolean enabled, String message) {
        if (enabled && loggingEnabled && logLevel.intValue() == Level.FINER.intValue()) {
            logger.finer("📃 " + message);
        }
    }
}
