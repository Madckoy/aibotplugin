package com.devone.bot.core.task.active.move;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.move.listeners.BotMoveTaskListener;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.*;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

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
            bot.getNavigator().setTarget(loc);
            BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Цель движения установлена: " + loc);
        } else {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Target Location is null! Invalid parameters.");
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {

        if (taskHandle != null && !taskHandle.isCancelled()) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⏳ Bot is moving...");
            return;
        }

        if (done || isPause()) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⭕ The task is done or paused...");
            return;
        }

        if (!bot.getNPC().isSpawned()) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⚠️ NPC не заспавнен!");
            this.stop();
            return;
        }

        BotLocation targetCoord = bot.getNavigator().getTarget();
        if (targetCoord == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Нет цели для движения!");
            this.stop();
            return;
        }

        Location targetLocation = BotWorldHelper.getWorldLocation(targetCoord);
        Block targetBlock = BotWorldHelper.getBlockAt(targetCoord);
        String blockName = BotUtils.getBlockName(targetBlock);
        String coordsStr = " " + targetCoord.getX() + ", " + targetCoord.getY() + ", " + targetCoord.getZ();

        setObjective(params.getObjective() + " to " + blockName + " at:" + coordsStr + " ("+ BotUtils.getRemainingTime(startTime, params.getTimeout()) +")");

        if (!isMoving) {
            if (listener == null) {
                listener = new BotMoveTaskListener(this);
                Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
            }

            bot.getNPCNavigator().getDefaultParameters().speedModifier(speed);

            MoveTaskHelper.setTarget(bot, params.getTarget(), params.getSpeed(), isLogging()); //bot.getNPCNavigator().setTarget(targetLocation);
            
            isMoving = true;

            BotLocation loc = BotWorldHelper.worldLocationToBotLocation(targetLocation);

            BotLogger.debug(icon, isLogging(), bot.getId() + " 🏃🏻‍♂️‍➡️ Начинает движение к " + loc);

            BotMoveTask mTask = this;

            taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {

                long rmt = BotUtils.getRemainingTime(startTime, params.getTimeout());

                setObjective(params.getObjective() + " to " + blockName + " at:" + coordsStr + " ("+ rmt +")");

                turnToTarget(mTask, loc);//new BotLocation(BotLocationHelper.convertFrom(targetLocation)));

                if (done || bot.getNPCEntity() == null) {
                    stopTaskHandle();
                    return;
                }

                if (rmt <= 0) {
                    BotLogger.debug(icon, isLogging(), bot.getId() + " ⏱️ Тайм-аут навигации.");
                    stopTaskHandle();
                    this.stop();
                }

            }, 0L, 40L); // раз в 2 секунд
        } else {

            BotLocation loc = BotWorldHelper.worldLocationToBotLocation(targetLocation);
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⏳ Уже в движении в " + loc);
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
        bot.getNavigator().setTarget(null);
        BotLogger.debug(icon, isLogging(), bot.getId() + " ⭕ Move task завершён");

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

        BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Навигатор сообщил о завершении");
        this.stop();
    }
}
