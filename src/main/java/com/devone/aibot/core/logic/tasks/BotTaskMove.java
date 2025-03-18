package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import org.bukkit.Material;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BotTaskMove extends BotTask {

    private BukkitTask taskHandle; // 🟢 Сохраняем ссылку на таймер, чтобы его остановить

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
            isDone = true;
        }
    }

    @Override
    public void executeTask() {

        BotLogger.debug(bot.getId() + " 🚦 Состояние семафоров: "+ isDone + isPaused + BotStringUtils.formatLocation(targetLocation) + " [Task ID: " + uuid + "]");

        if (taskHandle != null && !taskHandle.isCancelled()) {
            BotLogger.debug(bot.getId() + " ⏳ Таймер уже запущен, жду... [ID: " + uuid + "]");
        } else {

            if (isDone || isPaused || targetLocation == null) { // ✅ Фикс условия
                return;
            }

        }

        // 🟢 Запускаем таймер и сохраняем его в `taskHandle`
        taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (isDone) {
                if (taskHandle != null) {
                    taskHandle.cancel(); // ✅ Останавливаем таймер
                    BotLogger.debug(bot.getId() + " 🛑 Move task завершён, таймер остановлен. [ID: " + uuid + "]");
                }
                return;
            }

            // 1. Если бот уже движется, ждём следующего цикла
            if (bot.getNPCNavigator().isNavigating()) {
                return;
            }

            // 2. Проверяем, достиг ли бот цели
            if (BotNavigation.hasReachedTarget(bot, targetLocation, 1.5)) {
                bot.resetTargetLocation();
                isDone = true; // ✅ Теперь это действительно завершает задачу!
                BotLogger.debug(bot.getId() + " 🎯 Достиг цели! Реальная позиция: " + bot.getNPCEntity().getLocation() + " [ID: " + uuid + "]");
                return;
            }

            // 3. Получаем список доступных точек вокруг
            Map<Location, Material> scannedBlocks = EnvironmentScanner.scan3D(bot.getNPCEntity().getLocation(), 10);
            List<Location> validPoints = scannedBlocks.entrySet().stream()
                .filter(entry -> BotNavigation.isSuitableForNavigation(entry.getKey(), entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            if (validPoints.isEmpty()) {
                BotLogger.debug(bot.getId() + " ⚠️ Нет доступных точек для движения! Пробуем снова..." + " [ID: " + uuid + "]");
                return;
            }

            // 4. Выбираем ближайшую точку
            Location nextNavLoc = validPoints.stream()
                .min((loc1, loc2) -> Double.compare(loc1.distanceSquared(targetLocation), loc2.distanceSquared(targetLocation)))
                .orElse(targetLocation);

            // 5. Проверяем, может ли бот туда пройти
            if (!bot.getNPCNavigator().canNavigateTo(nextNavLoc)) {
                BotLogger.debug(bot.getId() + " ❌ Не могу найти путь, пробую пересканировать..." + " [ID: " + uuid + "]");
                return;
            }

            // 6. Двигаемся к следующей точке
            bot.getNPCNavigator().setTarget(nextNavLoc);
            // 
            BotLogger.debug(bot.getId() + " 🚶 Двигаюсь в " + BotStringUtils.formatLocation(nextNavLoc) + " [ID: " + uuid + "]");

        }, 0L, 20L); // ✅ Запускаем обновление навигации каждые 20 тиков (1 секунда)
    }
}
