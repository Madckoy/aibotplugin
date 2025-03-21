package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskMoveConfig;

public class BotTaskMove extends BotTask {

    private BukkitTask taskHandle;
    private static final BotTaskMoveConfig config = new BotTaskMoveConfig();
    private final float speedMultiplier = config.getSpeedMultiplier();

    private Location lastPosition; // 🆕 Запоминаем прошлую позицию
    private long lastMoveTime; // 🆕 Время последнего движения

    public BotTaskMove(Bot bot) {
        super(bot, "🏃🏽‍♂️‍➡️");
        this.lastPosition = bot.getNPCCurrentLocation();
        this.lastMoveTime = System.currentTimeMillis();
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
            if (isDone || isPaused) return;
        }

        if (targetLocation == null) {
            isDone = true;
            return;
        }

        if (bot.getNPCNavigator().isNavigating()) {
            return;
        }

        if (!bot.getNPCNavigator().canNavigateTo(getTargetLocation())) {
            BotLogger.trace(bot.getId() + " 🛑 Target Location is not reachable. Stopping here...[ID: " + uuid + "]");
            isDone = true;
            return;
        }

        String block_name = BotUtils.getBlockName(getTargetLocation().getBlock());
        setObjective("I can navigate, so I'm reaching the target... " + block_name);

        taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (isDone) {
                if (taskHandle != null) {
                    taskHandle.cancel();
                    BotLogger.debug(bot.getId() + " 🛑 Move task завершён, таймер остановлен. [ID: " + uuid + "]");
                }
                return;
            }

            // 🆕 Проверяем, двигается ли бот или застрял
            if (bot.getNPCCurrentLocation().distanceSquared(lastPosition) < 0.5) {
                // Если прошло > 10 сек и координаты не изменились → бот застрял
                if (System.currentTimeMillis() - lastMoveTime > 10_000) {
                    BotLogger.warn(bot.getId() + " ⚠️ Бот застрял! Пересчитываем путь...");
                    taskHandle.cancel();
                    isDone = true;
                    return;
                }
            } else {
                // Если бот сдвинулся — обновляем позицию и сбрасываем таймер
                lastPosition = bot.getNPCCurrentLocation();
                lastMoveTime = System.currentTimeMillis();
            }

            if (BotNavigationUtils.hasReachedTargetFlex(bot.getNPCCurrentLocation(), targetLocation, 1.5, 1.5)) {
                bot.resetTargetLocation();
                isDone = true;
                BotLogger.debug(bot.getId() + " 🎯 Достиг цели! Реальная позиция: " + bot.getNPCEntity().getLocation() + " [ID: " + uuid + "]");
                return;
            } else {
                if (!bot.getNPCNavigator().canNavigateTo(targetLocation)) {
                    BotLogger.trace(bot.getId() + " ❌ Не могу найти путь, Stopping here..." + " [ID: " + uuid + "]");
                    taskHandle.cancel();
                    isDone = true;
                    return;
                } else {
                    if (bot.getNPCEntity() == null) {
                        BotLogger.trace(bot.getId() + " 👻 Проблема с сущностью! В задаче ID: " + uuid + "]");
                        taskHandle.cancel();
                        isDone = true;
                    } else {
                        BotLogger.trace(bot.getId() + " 🚶 Двигаюсь в " + BotStringUtils.formatLocation(targetLocation) + " [ID: " + uuid + "]");

                        bot.getNPCNavigator().getDefaultParameters().speedModifier(speedMultiplier);

                        bot.getNPCCurrentLocation().setDirection(targetLocation.toVector().subtract(bot.getNPCCurrentLocation().toVector()));
                        bot.getNPCNavigator().setTarget(targetLocation);
                    }
                }
            }
        }, 0L, 40L);
    }
}
