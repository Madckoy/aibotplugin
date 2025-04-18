package com.devone.bot.core.logic.task.move;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.bot.blocks.BotLocationHelper;
import com.devone.bot.core.logic.task.BotTaskAutoParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.move.listeners.BotMoveTaskListener;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.*;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.plugin.AIBotPlugin;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;

public class BotMoveTask extends BotTaskAutoParams<BotMoveTaskParams> {

    private BukkitTask taskHandle;
    private boolean isMoving = false;
    private BotMoveTaskListener listener;
    private float speed = 1.0F;

    public BotMoveTask(Bot bot) {
        super(bot, BotMoveTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotMoveTaskParams> setParams(BotMoveTaskParams params) {
        super.setParams(params);

        BotLocation loc = params.getTarget();
        this.speed = params.getSpeed();
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        if (loc != null) {
            bot.getNavigation().setTarget(loc);
            BotLogger.debug("✅", isLogging(), bot.getId() + " Цель движения установлена: " + loc);
        } else {
            BotLogger.debug("❌", isLogging(), bot.getId() + " Target Location is null! Invalid parameters.");
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {
        if (taskHandle != null && !taskHandle.isCancelled()) return;
        if (isDone || isPaused) return;

        if (!bot.getNPC().isSpawned()) {
            BotLogger.debug("⚠️", isLogging(), bot.getId() + "NPC не заспавнен!");
            this.stop();
            return;
        }

        BotLocation targetCoord = bot.getNavigation().getTarget();
        if (targetCoord == null) {
            BotLogger.debug("❌", isLogging(), bot.getId() + "Нет цели для движения.");
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

            BotLogger.debug("🏃🏻‍♂️", isLogging(), bot.getId() + " Двигаюсь к " + targetLocation);

            taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {

                turnToTarget(new BotLocation(BotLocationHelper.convertFrom(targetLocation)));

                if (isDone || bot.getNPCEntity() == null) {
                    stopTaskHandle();
                    return;
                }

                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > BotConstants.DEFAULT_TASK_TIMEOUT) {
                    BotLogger.debug("⏱️", isLogging(), bot.getId() + " Тайм-аут навигации.");
                    bot.getState().setStuck(true);
                    stopTaskHandle();
                    this.stop();
                }

            }, 0L, 20L); // раз в секунду
        } else {
            BotLogger.debug("⏳", isLogging(), bot.getId() + " Двигаюсь к " + targetLocation);
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
        bot.getNavigation().setTarget(null);
        BotLogger.debug("🛑", isLogging(), bot.getId() + " Move task завершён");

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

        BotLogger.debug("✅", isLogging(), bot.getId() + " Навигатор сообщил о завершении");
        this.stop();
    }
}
