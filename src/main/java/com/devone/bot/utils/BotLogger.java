package com.devone.bot.utils;

import java.io.IOException;
import java.util.logging.*;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.config.AIBotPluginConfig;

public class BotLogger {

    private static final Logger logger = Logger.getLogger("AIBotPlugin");
    private static Handler fileHandler;
    private static boolean loggingEnabled = true;

    public static void init(AIBotPlugin plugin, AIBotPluginConfig config) {
        loggingEnabled = config.logging.enable;
        Level logLevel = Level.parse(config.logging.level);

        // ✅ Используем кастомный хэндлер
        try {
            fileHandler = new SimpleRollingFileHandler("plugins/AIBotPlugin/logs/console.log", 
                10 * 1024 * 1024, 5);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("❌ Ошибка инициализации логгера: " + e.getMessage());
        }

        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);

        info(true, "🔧 Logger initialized with level: " + logLevel.getName());
    }

    public static void debug(boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.fine("🟡 " + message);
        }
    }

    public static void info(boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.info("ℹ️ " + message);
        }
    }

    public static void warn(boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.warning("⚠️ " + message);
        }
    }

    public static void error(boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.severe("🚨 " + message);
        }
    }
}
