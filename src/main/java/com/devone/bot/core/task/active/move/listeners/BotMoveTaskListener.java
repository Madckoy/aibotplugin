package com.devone.bot.core.task.active.move.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.devone.bot.core.brain.memory.MemoryType;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.MoveTaskHelper;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.event.HandlerList;

import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;

public class BotMoveTaskListener implements Listener {

    private final BotMoveTask task;

    public BotMoveTaskListener(BotMoveTask task) {
        this.task = task;
    }

    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent event) {
        if (task.getBot().getNPC().getId() != event.getNPC().getId())
            return;

        BotLogger.debug(task.getIcon(), true,
                task.getBot().getId() + " ✅ Навигация завершена (BotMoveTaskListener), ID: " + task.getUUID());

        BotLocation target = task.getParams().getTarget();

        if (!MoveTaskHelper.isAtTarget(task.getBot(), target, 0)) {
            BotLogger.debug(task.getIcon(), true,
                    task.getBot().getId() + " ❌ Навигация завершилась, но NPC не достиг цели точно. Повторная попытка.");

            MoveTaskHelper.setTarget(task.getBot(), target, task.getParams().getSpeed(), true);
            
            return;
        }

        // ✅ Реально дошёл — продолжаем как раньше
        task.getBot().getNavigator().setStuck(false);

        // логика запоминания блока
        Block wBlock = BotWorldHelper.getBlockAt(target);
        BotBlockData block = new BotBlockData();
        block.setX(wBlock.getX());
        block.setY(wBlock.getY());
        block.setZ(wBlock.getZ());
        block.setType(wBlock.getType().toString());
        task.getBot().getBrain().getMemory().memorize(block, MemoryType.VISITED_BLOCKS);

        task.stop();
    }

    public void onNavigationCancel(NavigationCancelEvent event) {
        if (event.getNPC().getId() != task.getBot().getNPC().getId())
            return;

        BotLogger.debug(task.getIcon(), true,
                task.getBot().getId() + " ❌ Навигация отменена (BotMoveTaskListener) — NPC не смог дойти");
        task.stop();
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
