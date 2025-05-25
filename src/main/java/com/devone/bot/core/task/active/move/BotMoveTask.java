package com.devone.bot.core.task.active.move;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.move.listeners.BotMoveTaskListener;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.*;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

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

        this.speed = params.getSpeed();
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        //setEnabled(params.isEnabled());

        BotPosition target = params.getTarget();

        if (target == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Target is null. Остановка таски.");
            stop();
            return this;
        }

        bot.getNavigator().setTarget(target.toBlockData());
        BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Цель установлена: " + target);

        return this;
    }

    @Override
    public void execute() {

        if (done || isPause()) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⭕ Таска завершена или на паузе");
            return;
        }

        if (isMoving) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⏳ Уже в движении...");
            return;
        }

        if (!bot.getNPC().isSpawned()) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ⚠️ NPC не заспавнен");
            stop();
            return;
        }

        BotBlockData block = bot.getNavigator().getTarget();

        if(block==null) {
            stop();
            return;
        }

        BotPosition target = block.getPosition();
        
        if (target == null) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Цель навигации не найдена");
            stop();
            return;
        }

        Block targetBlock = BotWorldHelper.botPositionToWorldBlock(target);
        String blockName = BotUtils.getBlockName(targetBlock);

        setObjective(params.getObjective() + " to " + blockName + " at: " + target.toCompactString());

        // Навигация начинается
        if (listener == null) {
            listener = new BotMoveTaskListener(this);
            Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
        }

        BotMoveTaskHelper.setTarget(bot, target, speed, isLogging());

        isMoving = true;

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🏃 Начинаем движение к " + target);

        taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            long remaining = BotUtils.getRemainingTime(startTime, params.getTimeout());
            setObjective(params.getObjective() + " to " + blockName + " at: " + target.toCompactString() + " (" + remaining + ")");

            if (done || bot.getNPCEntity() == null) {
                stop();
                return;
            }

            turnToTarget(this, target);

            if (remaining <= 0) {
                BotLogger.debug(icon, isLogging(), bot.getId() + " ⏱️ Навигация превысила лимит времени");
                stop();
            }

        }, 0L, 40L); // раз в 2 секунды
    }

    @Override
    public void stop() {
        isMoving = false;
        stopTaskHandle();

        if (listener != null) {
            listener.unregister();
            listener = null;
        }

        bot.getNavigator().setTarget(null);
        BotLogger.debug(icon, isLogging(), bot.getId() + " ⭕ Движение остановлено");
        super.stop();
    }

    private void stopTaskHandle() {
        if (taskHandle != null && !taskHandle.isCancelled()) {
            taskHandle.cancel();
            taskHandle = null;
        }
    }

    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent event) {
        if (event.getNPC().getId() != bot.getNPC().getId()) return;

        BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Навигация завершена (BotMoveTask)");
        stop();
    }
}
