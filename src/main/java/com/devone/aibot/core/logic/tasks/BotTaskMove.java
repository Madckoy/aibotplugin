package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.devone.aibot.core.Bot;

public class BotTaskMove implements BotTask {
    private final Bot bot;
    private Location targetLocation;
    private boolean isDone = false;
    private boolean isPaused = false;
    private final String name = "MOVE";

    private long startTime;
    private static final long TIMEOUT = 10000; // 10 секундs

    private Location lastTargetLocation;

    public BotTaskMove(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void configure(Object... params) {
        if (params.length == 1 && params[0] instanceof Location) {
            this.targetLocation = (Location) params[0];
            lastTargetLocation = null;
            startTime = System.currentTimeMillis(); // ✅ Сбрасываем таймер при старте новой задачи
            isDone = false;
            BotLogger.info(" ⚙️ MoveTask is configured: " + BotStringUtils.formatLocation(targetLocation));
        } else {
            BotLogger.info("❌ Ошибка конфигурации MoveTask: неверные параметры");
        }
    }

    @Override
    public void update() {

        BotLogger.info("update(): "+bot.getId() + " Running task: " + name);

        if (Bukkit.getServer().isStopping()) {
            BotLogger.info("⚠️ " + bot.getId() + " Сервер выключается, отменяем обновление BotMoveTask.");
            return;
        }

        if (isDone ||
                isPaused ||
                targetLocation == null

        )
            return;

        // Проверяем, достиг ли бот цели
        if (BotNavigationUtils.hasReachedTarget(bot, targetLocation, 2.0)) {

            BotLogger.info("🎉" + bot.getId() + " Has reached the target: " + targetLocation);

            bot.resetTargetLocation();

            isDone = true;
            return;
        }

        Location currentLocation = bot.getNPCCurrentLocation();
        //
        // 3d scan
        BotScanEnv.scan3D(currentLocation, 5);
        //
        // pickup all items
        bot.pickupNearbyItems(true);
        //
        //
        BotLogger.info("📍 " + bot.getId() + " Current position is: " + BotStringUtils.formatLocation(currentLocation));
        BotLogger.info("🎯 " + bot.getId() + " Selected new target location: " + BotStringUtils.formatLocation(targetLocation));

        if (bot.getNPCNavigator().canNavigateTo(targetLocation)) {
            BotLogger.info("📌 " + bot.getId() + " Navigation point has accepted: " + BotStringUtils.formatLocation(targetLocation));
            // Навигация в основном потоке
            Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                bot.getNPCNavigator().setTarget(targetLocation);
            });

        } else {
            BotLogger.info("⚲ " + bot.getId() + " Can't navigate from " +
                    BotStringUtils.formatLocation(currentLocation) + " to "
                    + BotStringUtils.formatLocation(targetLocation));

            isDone = handleStuck();

        }

    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (isPaused) {
            BotLogger.info("꩜ " + bot.getId() + " ꩜ Pausing...");
        } else {
            BotLogger.info("▶️ " + bot.getId() + " ꩜ Resuming...");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public boolean handleStuck() {
        boolean return_state = false;

        BotLogger.info("🔄 " + bot.getId() + " Застрял! Пересчитываем маршрут...");

        // Пытаемся найти ближайшую доступную точку
        Location newTarget = BotNavigationUtils.findNearestNavigableLocation(bot.getNPCCurrentLocation(), targetLocation, 30);
        try {
            if (newTarget != null) {
                targetLocation = newTarget;
                BotLogger.info("🎯 " + bot.getId() + " 🛠 Новая цель: " + BotStringUtils.formatLocation(targetLocation));

                if(bot.getNPCNavigator().canNavigateTo(targetLocation)) {
                    bot.getNPCNavigator().setTarget(targetLocation);
                } else {
                    BotLogger.info("❌ " + bot.getId() + " Пробуем телепортировать в "+ BotStringUtils.formatLocation(targetLocation));
                    bot.getNPCEntity().teleport(targetLocation);
                    BotLogger.info("⚡ " + bot.getId() + " Телепортирован?");
                }

                // return_state = true; // // stop doing the active task

            } else {
                //--- hack
                BotLogger.info("❌ " + bot.getId() + " Не удалось найти маршрут. Пробуем телепортировать в "+ BotStringUtils.formatLocation(targetLocation));

                bot.getNPCEntity().teleport(targetLocation);

                BotLogger.info("⚡ " + bot.getId() + " Телепортирован?");
                //
                //return_state = false; // continue doing the active task
                //-------------
            }

        } catch (Exception ex) {
            BotLogger.error("⚠ " + bot.getId() + ex.getMessage());
            return_state = true; // stop doing the active task
        }
        return return_state;
    }

}
