package com.devone.aibot.utils;

import org.bukkit.configuration.file.FileConfiguration;
import com.devone.aibot.AIBotPlugin;
import java.io.IOException;
import java.util.logging.*;

public class BotLogger {
    private static final Logger logger = Logger.getLogger("AIBotPlugin");
    private static Level logLevel = Level.INFO;
    private static boolean loggingEnabled = true;
    private static FileHandler fileHandler; // 📂 Новый FileHandler
    private static final String LOG_FILE_PATH = "plugins/AIBotPlugin/logs/console.log";

    static {
        try {
            // 🛠 Настраиваем логирование в файл
            fileHandler = new FileHandler(LOG_FILE_PATH, 10 * 1024 * 1024, 5, true); // 10MB, 5 файлов
            fileHandler.setFormatter(new SimpleFormatter()); 
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);

            logger.setUseParentHandlers(false); // 🚫 Отключаем родительские обработчики (Console)
        } catch (IOException e) {
            System.err.println("❌ Ошибка инициализации логгера: " + e.getMessage());
        }
    }

    public static void init(AIBotPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        loggingEnabled = config.getBoolean("logging.enable", true);

        if (!loggingEnabled) {
            logLevel = Level.OFF;
            logger.setLevel(Level.OFF);
            fileHandler.setLevel(Level.OFF);
            return;
        }

        String levelStr = config.getString("logging.level", "SEVERE").toUpperCase();

        try {
            logLevel = Level.parse(levelStr);
            logger.setLevel(logLevel); 
            fileHandler.setLevel(logLevel);
            info("🔧 Установлен уровень логирования: " + logLevel.getName());
        } catch (IllegalArgumentException e) {
            logLevel = Level.SEVERE;
            logger.setLevel(Level.SEVERE);
            fileHandler.setLevel(Level.SEVERE);
            error("❌ Некорректный уровень логирования в config.yml, используется SEVERE.");
        }
    }

    public static void debug(String message) {
        if (loggingEnabled && logLevel.intValue() <= Level.FINE.intValue()) {
            logger.fine("🟡 " + message);
        }
    }

    public static void info(String message) {
        if (loggingEnabled && logLevel.intValue() <= Level.INFO.intValue()) {
            logger.info("ℹ️ " + message);
        }
    }

    public static void warn(String message) {
        if (loggingEnabled && logLevel.intValue() <= Level.WARNING.intValue()) {
            logger.warning("⚠️ " + message);
        }
    }

    public static void error(String message) {
        if (loggingEnabled && logLevel.intValue() <= Level.SEVERE.intValue()) {
            logger.severe("🚨 " + message);
        }
    }

    public static void trace(String message) {
        if (loggingEnabled && logLevel.intValue() <= Level.FINER.intValue()) {
            logger.finer("📃 " + message);
        }
    }
}
