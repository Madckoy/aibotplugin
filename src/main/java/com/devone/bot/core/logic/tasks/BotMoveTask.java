package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotMoveTaskConfig;
import com.devone.bot.utils.*;

public class BotMoveTask extends BotTask {

    private BukkitTask taskHandle;
    private static final BotMoveTaskConfig config = new BotMoveTaskConfig();
    private final float speedMultiplier = config.getSpeedMultiplier();

    private BotCoordinate3D lastPosition; // 🆕 Запоминаем прошлую позицию
    private long lastMoveTime; // 🆕 Время последнего движения

    public BotMoveTask(Bot bot) {
        super(bot, "🏃🏻‍♂️‍➡️");
        this.lastPosition = bot.getRuntimeStatus().getCurrentLocation();
        this.lastMoveTime = System.currentTimeMillis();

        setObjective("Move" );
        isLogged = config.isLogged();
    }

    @Override
    public BotTask configure(Object... params) {
        super.configure(params);

        if (params.length == 1 && params[0] instanceof BotCoordinate3D) {
            BotCoordinate3D loc = (BotCoordinate3D) params[0];
            
            bot.getRuntimeStatus().setTargetLocation(loc);

        } else {
            BotLogger.info(isLogged(),bot.getId() + " ❌ Некорректные параметры для `BotTaskMove`!");
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {
        if (taskHandle != null && !taskHandle.isCancelled()) {
            //BotLogger.info(bot.getId() + " ⏳ Таймер уже запущен, жду [ID: " + uuid + "]");
        } else {
            if (isDone || isPaused) return;
        }

        if (bot.getRuntimeStatus().getTargetLocation() == null) {
            this.stop();
            return;
        }

        if (bot.getNPCNavigator().isNavigating()) {
            BotLogger.info(this.isLogged(), " ⚠️ "+ bot.getId() + " В движении...");
        }

        Location targetLocation = BotWorldHelper.getWorldLocation(bot.getRuntimeStatus().getTargetLocation());

        if (!bot.getNPCNavigator().canNavigateTo(targetLocation)) {
            BotLogger.info(this.isLogged(), bot.getId() + " 🛑 Target Location is not reachable. Stopping where I am.[ID: " + uuid + "]");
            this.stop();
            return;
        }

        Block targetBlock = BotWorldHelper.getBlockAt(bot.getRuntimeStatus().getTargetLocation());

        String block_name = BotUtils.getBlockName(targetBlock);
        
        setObjective("Navigating to  " + block_name);

        taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            if (isDone) {
                if (taskHandle != null) {
                    taskHandle.cancel();
                    //BotLogger.info(bot.getId() + " 🛑 Move task завершён, таймер остановлен. [ID: " + uuid + "]");
                }
                return;
            }

            // 🆕 Проверяем, двигается ли бот или застрял
            if (bot.getRuntimeStatus().getCurrentLocation().equals(lastPosition)) {
                // Если прошло > 10 сек и координаты не изменились → бот застрял
                if (System.currentTimeMillis() - lastMoveTime > 10_000) {
                    BotLogger.warn(this.isLogged(),bot.getId() + " ⚠️ Бот застрял! Пересчитываем путь...");
                    taskHandle.cancel();
                    this.stop();
                    return;
                }
            } else {
                // Если бот сдвинулся — обновляем позицию и сбрасываем таймер
                lastPosition = bot.getRuntimeStatus().getCurrentLocation();
                lastMoveTime = System.currentTimeMillis();
            }

            if (BotNavigationUtils.hasReachedTarget(BotCoordinate3DHelper.convertFrom(targetLocation), bot.getRuntimeStatus().getTargetLocation())) {
                
                bot.getRuntimeStatus().setTargetLocation(null);

                this.stop();
                //BotLogger.info(bot.getId() + " 🎯 Достиг цели! Реальная позиция: " + bot.getNPCEntity().getLocation() + " [ID: " + uuid + "]");
                return;
            } else {
                if (!bot.getNPCNavigator().canNavigateTo(targetLocation)) {
                    //BotLogger.info(bot.getId() + " ❌ Не могу найти путь, Stopping where I am" + " [ID: " + uuid + "]");
                    taskHandle.cancel();
                    this.stop();
                    return;
                } else {
                    if (bot.getNPCEntity() == null) {
                        //BotLogger.info(bot.getId() + " 👻 Проблема с сущностью! В задаче ID: " + uuid + "]");
                        taskHandle.cancel();
                        this.stop();

                    } else {
                        BotLogger.info(this.isLogged(), bot.getId() + " 🚶 Двигаюсь в " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [ID: " + uuid + "]");

                        bot.getNPCNavigator().getDefaultParameters().speedModifier(speedMultiplier);
                        
                        //bot.getRuntimeStatus().getCurrentLocation().setDirection(bot.getRuntimeStatus().getTargetLocation().toVector().subtract(bot.getRuntimeStatus().getCurrentLocation().toVector()));
                        
                        bot.getNPCNavigator().setTarget(targetLocation);

                    }
                }
            }
        }, 0L, 40L);
    }

    @Override
    public void stop() {
        this.isDone = true;
        BotLogger.info(this.isLogged(), bot.getId() + " 🛑 Move task завершён [ID: " + uuid + "]");  

    }
}
