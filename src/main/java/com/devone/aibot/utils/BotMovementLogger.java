package com.devone.aibot.utils;

import com.devone.aibot.core.Bot;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BotMovementLogger {
    private static final String LOG_FOLDER = "plugins/AIBotPlugin/logs/";
    private static final Map<String, Location> lastLoggedLocations = new HashMap<>();
    private static final String SESSION_ID = generateSessionId();

    public static void logBotMovement(Bot bot) {
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

            String logEntry = String.format("%s,%s,%s,%d,%d,%d",
                    getCurrentTimestamp(), botName, loc.getWorld().getName(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

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

            bw.write("timestamp,bot_id,world,x,y,z");
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
