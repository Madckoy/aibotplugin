package com.devone.aibot.utils;

import org.bukkit.configuration.file.FileConfiguration;
import com.devone.aibot.AIBotPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BotLogger {
    private static Logger logger;
    private static Level logLevel = Level.INFO;

    public static void init(AIBotPlugin plugin) {
        logger = plugin.getLogger();
        FileConfiguration config = plugin.getConfig();

        try {
            logLevel = Level.parse(config.getString("logging.level", "INFO").toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warning("Некорректный уровень логирования в config.yml, используется INFO.");
            logLevel = Level.INFO;
        }
    }

    public static void debug(String message) {
        if (logLevel.intValue() <= Level.FINE.intValue()) {
            logger.info("[DEBUG] " + message);
        }
    }

    public static void info(String message) {
        if (logLevel.intValue() <= Level.INFO.intValue()) {
            logger.info("[INFO] " + message);
        }
    }

    public static void warning(String message) {
        logger.severe("[WARN] " + message);
    }

    public static void error(String message) {
        logger.severe("[ERROR] " + message);
    }
}
