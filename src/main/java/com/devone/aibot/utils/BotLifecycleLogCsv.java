package com.devone.aibot.utils;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.BotTask;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BotLifecycleLogCsv {
    private static final String LOG_FOLDER = BotConstants.PLUGIN_PATH_LOGS;//"plugins/AIBotPlugin/logs/";
    private static final Map<String, Location> lastLoggedLocations = new HashMap<>();
    private static final String SESSION_ID = generateSessionId();

    public static void write(Bot bot) {
        Location loc = bot.getNPCCurrentLocation();
        if (loc == null) return;

        String botName = bot.getId();
        String filename = LOG_FOLDER + botName + "_session_" + SESSION_ID + ".csv";

        // Если файл не существует, создаем его и пишем заголовки
        File logFile = new File(filename);
        if (!logFile.exists()) {
            writeHeader(logFile);
        }

        // Проверяем, изменилось ли местоположение
        Location lastLoc = lastLoggedLocations.get(botName);
        if (lastLoc != null && lastLoc.distance(loc) < 3) {
            return; // Пропускаем незначительные движения
        }

        // Записываем в CSV
        try (FileWriter writer = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(writer)) {

            BotTask task = bot.getCurrentTask();

            String t_name = "N/A";
            String e_time = "N/A";

            if(task!=null) {
                t_name = task.getName();
                e_time = BotStringUtils.formatTime(task.getElapsedTime());
            }

            String logEntry = String.format("%s,%s,%s,%d,%d,%d,%s,%s",
                    getCurrentTimestamp(), botName, loc.getWorld().getName(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),"'"+t_name+"'", e_time);

            bw.write(logEntry);
            bw.newLine();

            // Обновляем кешированные координаты
            lastLoggedLocations.put(botName, loc.clone());

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
