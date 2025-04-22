package com.devone.bot.core.task.active.move.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.devone.bot.core.brain.memory.MemoryType;
import com.devone.bot.core.task.active.move.BotMoveTask;
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

        BotLocation target = task.getBot().getNavigator().getTarget();
        BotLocation current = BotWorldHelper.worldLocationToBotLocation(task.getBot().getNPC().getEntity().getLocation());

        // Проверка — действительно ли бот дошёл
        if (!BlockUtils.isSamePosition(current, target)) {
            BotLogger.debug(task.getIcon(), true,
                    task.getBot().getId() + " ❗ Навигация завершена, но координаты не совпадают! "
                            + "Цель: " + target + " | Текущая: " + current);

            // Пробуем повторно задать цель
            task.getBot().getNPCNavigator().setTarget(BotWorldHelper.getWorldLocation(target));
            return;
        }

        task.getBot().getNavigator().setStuck(false);

        // ✅ Дошли — можно запомнить блок
        Block wBlock = BotWorldHelper.getBlockAt(current);
        BotBlockData block = new BotBlockData();
        block.setX(wBlock.getX());
        block.setY(wBlock.getY());
        block.setZ(wBlock.getZ());
        block.setType(wBlock.getType().toString());

        task.getBot().getBrain().getMemory().memorize(block, MemoryType.VISITED_BLOCKS);

        task.stop(); // Завершаем только если точно дошли
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
