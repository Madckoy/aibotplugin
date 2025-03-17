package com.devone.aibot.utils;

import org.bukkit.configuration.file.FileConfiguration;
import com.devone.aibot.AIBotPlugin;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BotLogger {
    private static final Logger logger = Logger.getLogger("AIBotPlugin"); // 🆕 Создаём отдельный логгер
    private static Level logLevel = Level.INFO;
    private static boolean loggingEnabled = true;

    static {
        logger.setUseParentHandlers(false); // 🛑 Отключаем наследование от глобального PaperMC логгера
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.setLevel(logLevel);
    }

    public static void init(AIBotPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        loggingEnabled = config.getBoolean("logging.enable", true);

        if (!loggingEnabled) {
            logLevel = Level.OFF;
            return;
        }

        String levelStr = config.getString("logging.level", "SEVERE").toUpperCase();

        try {
            logLevel = Level.parse(levelStr);
            logger.setLevel(logLevel); // 🆕 Теперь логгер использует этот уровень
            info("🔧 Установлен уровень логирования: " + logLevel.getName());
        } catch (IllegalArgumentException e) {
            logLevel = Level.SEVERE;
            error("❌ Некорректный уровень логирования в config.yml, используется SEVERE.");
        }
    }

    public static void debug(String message) {
        if (loggingEnabled && logLevel.intValue() == Level.FINE.intValue()) {
            logger.fine("🐌 " + message);
        }
    }

    public static void info(String message) {
        if (loggingEnabled && logLevel.intValue() == Level.INFO.intValue()) {
            logger.info("ℹ️ " + message);
        }
    }

    public static void warn(String message) {
        if (loggingEnabled && logLevel.intValue() == Level.WARNING.intValue()) {
            logger.warning("⚠️ " + message);
        }
    }

    public static void error(String message) {
        if (loggingEnabled && logLevel.intValue() == Level.SEVERE.intValue()) {
            logger.severe("🚨 " + message);
        }
    }
}
