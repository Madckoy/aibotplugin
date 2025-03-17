package com.devone.aibot.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.BotTaskMove;

import java.util.*;
import java.util.stream.Collectors;

public class BotNavigation {
    
    private static final Random random = new Random();
    private static final int MAX_STUCK_TIME = 5000; // 5 секунд застревания
    private static final double MIN_MOVEMENT_THRESHOLD = 0.1; // Минимальный порог движения
    private static final double POSITION_TOLERANCE = 1.5; // Допустимое отклонение

    public static void navigateTo(Bot bot, Location target, int scanRadius) {
        final Location[] currentLocation = {bot.getNPCEntity().getLocation()};
        final long[] stuckStartTime = {System.currentTimeMillis()};
        Queue<Location> pathQueue = new LinkedList<>();
        pathQueue.add(currentLocation[0]);

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (pathQueue.isEmpty()) return;

            Location current = pathQueue.poll();
            Map<Location, Material> scannedBlocks = BotScanEnv.scan3D(current, scanRadius);

            List<Location> validPoints = scannedBlocks.entrySet().stream()
                .filter(entry -> isSuitableForNavigation(entry.getKey(), entry.getValue()))
                .map(Map.Entry::getKey)
                .filter(loc -> loc.distanceSquared(currentLocation[0]) > POSITION_TOLERANCE * POSITION_TOLERANCE) // ✅ Исключаем слишком близкие точки
                .collect(Collectors.toList());

            if (validPoints.isEmpty()) {
                BotLogger.warn(bot.getId() + " ⚠️ Нет доступных точек для движения! Пробую перегенерировать маршрут...");
                pathQueue.clear(); // ✅ Очищаем маршрут и пробуем заново

                navigateTo(bot, target, scanRadius); // ✅ Пробуем снова/ !!!! ТУТ РЕКУРСИЯ! ОПАСНО!
                
                return;
            }

            // Пересчитываем маршрут
            Location nextStep = validPoints.stream()
                .min(Comparator.comparingDouble(loc -> loc.distanceSquared(target)))
                .orElse(target);

            // ✅ Если бот уже достиг цели, очищаем очередь задач
            if (hasReachedTarget(bot, nextStep, 0.5)) {
                pathQueue.clear();
                BotLogger.info(bot.getId() + " 🎯 Достиг цели, останавливаюсь.");
                return;
            }

            pathQueue.add(nextStep);

            // ✅ Теперь бот не телепортируется, а реально идёт!
            BotTaskMove nextMove = new BotTaskMove(bot); 
            nextMove.configure(nextStep);
            bot.getLifeCycle().getTaskStackManager().pushTask(nextMove);
          
            
            // Проверяем, застрял ли бот
            if (bot.getNPCEntity().getLocation().distanceSquared(currentLocation[0]) < MIN_MOVEMENT_THRESHOLD) {
                if (System.currentTimeMillis() - stuckStartTime[0] > MAX_STUCK_TIME) {
                    BotLogger.error(bot.getId() + " ⛔ Бот застрял! Пробую перестроить маршрут.");
                    pathQueue.clear();
                    navigateTo(bot, target, scanRadius); // ✅ Вместо телепортации пробуем найти другой путь
                    return;
                }
            } else {
                stuckStartTime[0] = System.currentTimeMillis();
                currentLocation[0] = bot.getNPCEntity().getLocation();
            }
        }, 0L, 40L); // ✅ Запускаем реже (каждые 2 секунды)
    }

    public static Location getRandomPatrolPoint(Bot bot, int scanRadius) {
        Location currentLocation = bot.getNPCEntity().getLocation();
        Map<Location, Material> scannedBlocks = BotScanEnv.scan3D(currentLocation, scanRadius);

        List<Location> validPoints = scannedBlocks.entrySet().stream()
            .filter(entry -> isSuitableForNavigation(entry.getKey(), entry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        if (validPoints.isEmpty()) {
            return currentLocation; // Если нет доступных точек, остаёмся на месте
        }

        return validPoints.stream()
            .min(Comparator.comparingDouble(loc -> loc.distanceSquared(currentLocation)))
            .orElse(currentLocation);
    }

    public static boolean hasReachedTarget(Bot bot, Location target, double tolerance) {
        Location current = bot.getNPCCurrentLocation();

        if (current == null || target == null) {
            BotLogger.error("❌ Ошибка: hasReachedTarget() вызван с null-координатами!");
            return false;
        }

        if (!current.getWorld().equals(target.getWorld())) {
            BotLogger.error("❌ " + bot.getId()+ " Ошибка: hasReachedTarget() вызван для разных миров!");
            return false;
        }

        double distanceSquared = current.distanceSquared(target);
        double yDifference = Math.abs(current.getY() - target.getY());

        if (distanceSquared <= tolerance * tolerance && yDifference < 2.0) {
            BotLogger.debug("✅ " + bot.getId()+ " Бот достиг цели! " + BotStringUtils.formatLocation(current));
            return true;
        }

        return false;
    }

    private static boolean isSuitableForNavigation(Location location, Material material) {
        return material.isSolid() && location.clone().add(0, 1, 0).getBlock().getType() == Material.AIR
            && material != Material.LAVA;
    }
}
