package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;

public class BotTaskMove extends BotTask {

    private BukkitTask taskHandle; // 🟢 Сохраняем ссылку на таймер, чтобы его остановить

    public BotTaskMove(Bot bot) {
        super(bot, "🏃🏽‍♂️‍➡️");
    }

    @Override
    public BotTask configure(Object... params) {
        super.configure(params);

        if (params.length == 1 && params[0] instanceof Location) {

            this.targetLocation = (Location) params[0];

        } else {

            BotLogger.error(bot.getId() + " ❌ Некорректные параметры для `BotTaskMove`!");
            isDone = true;
        }

        return this;
    }

    @Override
    public void executeTask() {

        if (taskHandle != null && !taskHandle.isCancelled()) {
            BotLogger.debug(bot.getId() + " ⏳ Таймер уже запущен, жду... [ID: " + uuid + "]");
        } else {

            if (isDone || isPaused ) { // ✅ Фикс условия
                return;
            }

        }
        
        if (targetLocation == null) {
            isDone = true;
            return;
        }

        // 1. Если бот уже движется, ждём следующего цикла
        if (bot.getNPCNavigator().isNavigating()) {
            return;
        }

        if(bot.getNPCNavigator().canNavigateTo(getTargetLocation()) == false) {
            BotLogger.trace(bot.getId() + " 🛑 Target Location is not reachable. Stopping here...[ID: " + uuid + "]");
            handleStuck();
            return;
        }

        String block_name  = BotUtils.getBlockName(getTargetLocation().getBlock());

        setObjective("Moving on ... " +  block_name);

        // 🟢 Запускаем таймер и сохраняем его в `taskHandle`
        taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (isDone) {
                if (taskHandle != null) {
                    taskHandle.cancel(); // ✅ Останавливаем таймер
                    BotLogger.debug(bot.getId() + " 🛑 Move task завершён, таймер остановлен. [ID: " + uuid + "]");
                }
                return;
            }

            // 2. Проверяем, достиг ли бот цели
            if (BotNavigationUtils.hasReachedTarget(bot, targetLocation, 10)) {
                bot.resetTargetLocation();
                isDone = true; // ✅ Теперь это действительно завершает задачу!
                BotLogger.debug(bot.getId() + " 🎯 Достиг цели! Реальная позиция: " + bot.getNPCEntity().getLocation() + " [ID: " + uuid + "]");
                return;
            } else {
                // 5. Проверяем, может ли бот туда пройти
                if (!bot.getNPCNavigator().canNavigateTo(targetLocation)) {
                    BotLogger.trace(bot.getId() + " ❌ Не могу найти путь, Stopping here..." + " [ID: " + uuid + "]");

                    handleStuck();

                    return;
                
                } else {
                   
                    if(bot.getNPCEntity() ==null) {

                        BotLogger.trace(bot.getId() + " 👻 Проблема с сущьностью! В задаче ID: " + uuid + "]");
                    
                        taskHandle.cancel(); // ✅ Останавливаем таймер
                        isDone = true; // останавливаем  задачу

                    } else {

                        BotLogger.trace(bot.getId() + " 🚶 Двигаюсь в " + BotStringUtils.formatLocation(targetLocation) + " [ID: " + uuid + "]");

                        bot.getNPCCurrentLocation().setDirection(targetLocation.toVector().subtract(bot.getNPCCurrentLocation().toVector()));

                        bot.getNPCNavigator().setTarget(targetLocation);
                        
                    }
            }
        }

        }, 0L, 40L); // ✅ Запускаем обновление навигации каждые 20 тиков (1 секунда)
    }
}
