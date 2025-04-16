package com.devone.bot.core.logic.task.move;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.move.listeners.BotMoveTaskListener;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.utils.*;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.blocks.BotLocationHelper;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;

public class BotMoveTask extends BotTask<BotMoveTaskParams> {

    private BukkitTask taskHandle;
    private boolean isMoving = false;
    private BotMoveTaskListener listener;
    private float speed = 1.0F;

    public BotMoveTask(Bot bot) {
        super(bot);
        setParams(new BotMoveTaskParams()); // загрузка параметров из JSON
    }

    @Override
    public IBotTaskParameterized<BotMoveTaskParams> setParams(BotMoveTaskParams params) {
        super.setParams(params);

        BotLocation loc = params.getTarget();
        this.speed = params.getSpeed();
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        if (loc != null) {
            bot.getRuntimeStatus().setTargetLocation(loc);
            BotLogger.info("✅", isLogging(), bot.getId() + " Цель движения установлена: " + loc);
        } else {
            BotLogger.info("❌", isLogging(), bot.getId() + " Target Location is null! Invalid parameters.");
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {
        if (taskHandle != null && !taskHandle.isCancelled()) return;
        if (isDone || isPaused) return;

        if (!bot.getNPC().isSpawned()) {
            BotLogger.info("⚠️", isLogging(), bot.getId() + "NPC не заспавнен!");
            this.stop();
            return;
        }

        BotLocation targetCoord = bot.getRuntimeStatus().getTargetLocation();
        if (targetCoord == null) {
            BotLogger.info("❌", isLogging(), bot.getId() + "Нет цели для движения.");
            this.stop();
            return;
        }

        Location targetLocation = BotWorldHelper.getWorldLocation(targetCoord);
        Block targetBlock = BotWorldHelper.getBlockAt(targetCoord);
        String blockName = BotUtils.getBlockName(targetBlock);
        String coordsStr = " " + targetCoord.getX() + ", " + targetCoord.getY() + ", " + targetCoord.getZ();

        setObjective(params.getObjective() + " to " + blockName + " at:" + coordsStr);

        if (!isMoving) {
            if (listener == null) {
                listener = new BotMoveTaskListener(this);
                Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
            }

            bot.getNPCNavigator().getDefaultParameters().speedModifier(speed);
            bot.getNPCNavigator().setTarget(targetLocation);
            isMoving = true;

            BotLogger.info("🏃🏻‍♂️", isLogging(), bot.getId() + " Двигаюсь к " + targetLocation);

            taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {

                turnToTarget(new BotLocation(BotLocationHelper.convertFrom(targetLocation)));

                if (isDone || bot.getNPCEntity() == null) {
                    stopTaskHandle();
                    return;
                }

                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > BotConstants.DEFAULT_TASK_TIMEOUT) {
                    BotLogger.info("⏱️", isLogging(), bot.getId() + " Тайм-аут навигации.");
                    bot.getRuntimeStatus().setStuck(true);
                    stopTaskHandle();
                    this.stop();
                }

            }, 0L, 20L); // раз в секунду
        } else {
            BotLogger.info("⏳", isLogging(), bot.getId() + " Двигаюсь к " + targetLocation);
        }
    }

    private void stopTaskHandle() {
        if (taskHandle != null && !taskHandle.isCancelled()) {
            taskHandle.cancel();
            taskHandle = null;
        }
    }

    @Override
    public void stop() {
        isMoving = false;
        bot.getRuntimeStatus().setTargetLocation(null);
        BotLogger.info("🛑", isLogging(), bot.getId() + " Move task завершён");

        stopTaskHandle();

        if (listener != null) {
            listener.unregister();
            listener = null;
        }

        super.stop();
    }

    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent event) {
        if (event.getNPC().getId() != bot.getNPC().getId()) return;

        BotLogger.info("✅", isLogging(), bot.getId() + " Навигатор сообщил о завершении");
        this.stop();
    }
}
