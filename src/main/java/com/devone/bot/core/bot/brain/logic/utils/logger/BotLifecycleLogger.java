package com.devone.bot.core.bot.brain.logic.utils.logger;

import org.bukkit.Bukkit;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.task.BotTask;
import com.devone.bot.core.bot.brain.logic.utils.BotConstants;
import com.devone.bot.core.bot.brain.logic.utils.BotUtils;
import com.devone.bot.core.bot.utils.blocks.BotLocation;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotLifecycleLogger {
    private static final String LOG_FOLDER = BotConstants.PLUGIN_TMP; // Путь к логам

    private static final String SESSION_ID = generateSessionId();

    public static void write(Bot bot) {
        BotLocation loc = bot.getNavigation().getLocation();
        if (loc == null) return;
    
        String botName = bot.getId();
        String filename = LOG_FOLDER + botName + "_moves_" + SESSION_ID + ".csv";
    
        File logFile = new File(filename);
    
        // ✅ Создание директорий
        logFile.getParentFile().mkdirs();
    
        if (!logFile.exists()) {
            writeHeader(logFile);
        }
    
        try (FileWriter writer = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(writer)) {
    
            BotTask<?> task = bot.getBrain().getCurrentTask();
    
            String t_name = "N/A";
            String e_time = "N/A";
    
            if (task != null) {
                t_name = task.getIcon();
                e_time = BotUtils.formatTime(task.getElapsedTime());
            }
    
            String logEntry = String.format("%s,%s,%s,%d,%d,%d,%s,%s",
                    getCurrentTimestamp(), botName, Bukkit.getWorlds().get(0).getName(),
                    loc.getX(), loc.getY(), loc.getZ(), "'" + t_name + "'", e_time);
    
            bw.write(logEntry);
            bw.newLine();
    
        } catch (IOException e) {
            Bukkit.getLogger().warning("[BotMovementLogger] Ошибка записи в лог " + filename + ": " + e.getMessage());
        }
    }

    private static void writeHeader(File file) {
        try (FileWriter writer = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write("timestamp,bot_id,world,x,y,z,action,duration");
            bw.newLine();

        } catch (IOException e) {
            Bukkit.getLogger().warning("[BotMovementLogger] Ошибка создания файла " + file.getName() + ": " + e.getMessage());
        }
    }

    private static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static String generateSessionId() {
        return String.valueOf(System.currentTimeMillis() / 1000); // Простой session_id на основе времени
    }
}
