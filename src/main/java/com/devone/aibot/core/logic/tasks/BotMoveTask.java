package com.devone.aibot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.BlockScanner3D;

public class BotMoveTask implements BotTask {
    private final Bot bot;
    private Location targetLocation;
    private boolean isDone = false;
    private boolean isPaused = false;
    private String name = "MOVE";

    private long startTime;
    private static final long TIMEOUT = 10000; // 10 секундs

    private Location lastTargetLocation;

    public BotMoveTask(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void configure(Object... params) {
        if (params.length == 1 && params[0] instanceof Location) {
            this.targetLocation = (Location) params[0];
            lastTargetLocation = null;
            startTime = System.currentTimeMillis(); // ✅ Сбрасываем таймер при старте новой задачи
            isDone = false;
            BotLogger.debug(" ⚙️ MoveTask is configured: " + BotUtils.formatLocation(targetLocation));
        } else {
            BotLogger.debug("❌ Ошибка конфигурации MoveTask: неверные параметры");
        }
    }

    @Override
    public void update() {

        if (Bukkit.getServer().isStopping()) {
            BotLogger.info(bot.getId() + " ⚠️ Сервер выключается, отменяем обновление BotMoveTask.");
            return;
        }
        

        BotLogger.info("BotMoveTask:update()");

        if (isDone || 
            isPaused || 
            targetLocation == null

            ) return;

        // Проверяем, достиг ли бот цели
        if (BotUtils.hasReachedTarget(bot.getNPCCurrentLocation(), targetLocation, 2.0)) {

            BotLogger.info(bot.getId() + " 🎉 Has reached the target: "+targetLocation);

            bot.resetTargetLocation();

            isDone = true;
            return;
        }

        Location currentLocation = bot.getNPCCurrentLocation();
        //
        // 3d scan
        BlockScanner3D.scanSurroundings(currentLocation, 4);
        //
        // pickup all items
        bot.pickupNearbyItems();
        //
        //
        BotLogger.debug(bot.getId() + " 📍 Current position is: " + BotUtils.formatLocation(currentLocation));
        BotLogger.debug(bot.getId() + " 🎯 Target location is: " + BotUtils.formatLocation(targetLocation));

        if(bot.getNPCNavigator().canNavigateTo(targetLocation)) {
            // Навигация в основном потоке
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("AIBotPlugin"), () -> {
                // Логика движения
                BotLogger.info(bot.getId() + " Moving to " + BotUtils.formatLocation(targetLocation));

                bot.getNPCNavigator().setTarget(targetLocation);

                BotLogger.info(bot.getId()+" 📌 Navigation point has accepted: " + BotUtils.formatLocation(targetLocation));
            });
        } else {
            BotLogger.info(bot.getId() + " ⚲ Can't navigate from "+BotUtils.formatLocation(currentLocation)+" to " + BotUtils.formatLocation(targetLocation));

            bot.resetTargetLocation();
            isDone = true;
        }

    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        BotLogger.debug(bot.getId() + (paused ? " ꩜ Waiting" : " ▶️ Resuming"));
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

    public void handleStuck() {
        BotLogger.info(bot.getId() + " 🔄 Бот застрял! Пересчитываем маршрут...");
    
        // Пытаемся найти ближайшую доступную точку
        Location newTarget = BotUtils.findNearestNavigableLocation(bot.getNPCCurrentLocation(), targetLocation, 5);
        
        if (newTarget != null) {
            targetLocation = newTarget;
            BotLogger.info(bot.getId() + " 🛠 Новая цель: " + BotUtils.formatLocation(targetLocation));
            bot.getNPCNavigator().setTarget(targetLocation);
        } else {
            BotLogger.error(bot.getId() + " ❌ Не удалось найти маршрут. Телепортируем...");
            bot.getNPCEntity().teleport(targetLocation);


        }
    }

}
