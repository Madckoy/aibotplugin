package com.devone.bot.utils.logger;

import org.bukkit.Bukkit;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.utils.BotConstants;

import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotCoordinate3D;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotLifecycleLogger {
    private static final String LOG_FOLDER = BotConstants.PLUGIN_TMP; // Путь к логам

    private static final String SESSION_ID = generateSessionId();

    public static void write(Bot bot) {
        // Получаем текущую локацию через BotRuntimeStatus
        BotCoordinate3D loc = bot.getRuntimeStatus().getCurrentLocation();
        if (loc == null) return;

        String botName = bot.getId();
        String filename = LOG_FOLDER + botName + "_movements_" + SESSION_ID + ".csv";

        // Если файл не существует, создаем его и пишем заголовки
        File logFile = new File(filename);
        if (!logFile.exists()) {
            writeHeader(logFile);
        }

        // Записываем в CSV
        try (FileWriter writer = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(writer)) {

            BotTask task = bot.getRuntimeStatus().getCurrentTask();

            String t_name = "N/A";
            String e_time = "N/A";

            if (task != null) {
                t_name = task.getName();
                e_time = BotUtils.formatTime(task.getElapsedTime());
            }

            String logEntry = String.format("%s,%s,%s,%d,%d,%d,%s,%s",
                    getCurrentTimestamp(), botName, Bukkit.getWorlds().get(0).getName(),
                    loc.x, loc.y, loc.z, "'" + t_name + "'", e_time);

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
