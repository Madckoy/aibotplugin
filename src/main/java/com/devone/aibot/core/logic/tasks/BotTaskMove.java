package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import org.bukkit.Material;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BotTaskMove extends BotTask {

    private Location targetLocation;

    public BotTaskMove(Bot bot) {
        super(bot, "MOVE");
    }

    @Override
    public void configure(Object... params) {
        super.configure(params);
        if (params.length == 1 && params[0] instanceof Location) {
            this.targetLocation = (Location) params[0];
        } else {
            BotLogger.error(bot.getId() + " ❌ Некорректные параметры для `BotTaskMove`!");
            isDone = true; // Завершаем задачу, если переданы неправильные параметры
        }
    }

    @Override
    public void executeTask() {
        if (isDone || isPaused || targetLocation == null) {
            return;
        }

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            // 1. Если бот уже движется, ждём следующего цикла
            if (bot.getNPCNavigator().isNavigating()) {
                return;
            }

            // 2. Проверяем, достиг ли бот цели
            if (BotNavigation.hasReachedTarget(bot, targetLocation, 1.5)) {
                bot.resetTargetLocation();
                isDone = true;
                BotLogger.info(bot.getId() + " 🎯 Достиг цели!");
                return;
            }

            // 3. Получаем список доступных точек вокруг
            Map<Location, Material> scannedBlocks = BotScanEnv.scan3D(bot.getNPCEntity().getLocation(), 10);
            List<Location> validPoints = scannedBlocks.entrySet().stream()
                .filter(entry -> BotNavigation.isSuitableForNavigation(entry.getKey(), entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            if (validPoints.isEmpty()) {
                BotLogger.warn(bot.getId() + " ⚠️ Нет доступных точек для движения! Пробуем снова...");
                return; // Остаёмся в цикле, пока маршрут не появится
            }

            // 4. Выбираем ближайшую точку
            Location nextNavLoc = validPoints.stream()
                .min((loc1, loc2) -> Double.compare(loc1.distanceSquared(targetLocation), loc2.distanceSquared(targetLocation)))
                .orElse(targetLocation);

            // 5. Проверяем, может ли бот туда пройти
            if (!bot.getNPCNavigator().canNavigateTo(nextNavLoc)) {
                BotLogger.warn(bot.getId() + " ❌ Не могу найти путь, пробую пересканировать...");
                return;
            }

            // 6. Двигаемся к следующей точке
            bot.getNPCNavigator().setTarget(nextNavLoc);
            BotLogger.debug(bot.getId() + " 🚶 Двигаюсь в " + BotStringUtils.formatLocation(nextNavLoc));

        }, 0L, 20L); // ✅ Запускаем обновление навигации каждые 20 тиков (1 секунда)
    }
}
