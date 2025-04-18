package com.devone.bot.utils.logger;

import java.io.IOException;
import java.util.logging.*;

import com.devone.bot.core.plugin.AIBotPlugin;
import com.devone.bot.core.plugin.config.AIBotPluginConfig;

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
                10 * 1024 * 1024, 5, true);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("❌ Ошибка инициализации логгера: " + e.getMessage());
        }

        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);

        info("🔧 BotLogger", true, "Logger initialized with level: " + logLevel.getName());
    }

    public static void debug(String method, boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.fine("🟡" +" "+ method + " "+message);
        }
    }

    public static void info(String method, boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.info("ℹ️"+" "+ method + " "+message);
        }
    }

    public static void warn(String method, boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.warning("⚠️" +" "+ method + " "+message);
        }
    }

    public static void error(String method, boolean enabled, String message) {
        if (enabled && loggingEnabled) {
            logger.severe("🚨"+" "+ method + " "+ message);
        }
    }
}
