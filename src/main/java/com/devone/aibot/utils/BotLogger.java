package com.devone.aibot.utils;

import org.bukkit.configuration.file.FileConfiguration;
import com.devone.aibot.AIBotPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BotLogger {
    private static Logger logger;
    private static Level logLevel = Level.OFF;

    public static void init(AIBotPlugin plugin) {
        logger = plugin.getLogger();
        FileConfiguration config = plugin.getConfig();

        try {
            logLevel = Level.parse(config.getString("logging.level", "OFF").toUpperCase());
        } catch (IllegalArgumentException e) {
            logLevel = Level.INFO;
            warn("Некорректный уровень логирования в config.yml, используется OFF.");
            logLevel = Level.OFF;
        }
    }

    public static void debug(String message) {
        if (logLevel.intValue() <= Level.FINE.intValue()) {
            logger.info("🐌 " + message);
        }
    }

    public static void info(String message) {
        if (logLevel.intValue() <= Level.INFO.intValue()) {
            logger.info("ℹ️ " + message);
        }
    }

    public static void warn(String message) {
        if (logLevel.intValue() <= Level.WARNING.intValue()) {
            logger.severe("⚠️ " + message);
        }
    }

    public static void error(String message) {
        if (logLevel.intValue() <= Level.SEVERE.intValue()) {
            logger.severe("🚨 " + message);
        }
    }
}
