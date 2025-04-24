package com.devone.bot.core.task.active.move.listeners;

import com.devone.bot.core.brain.memory.MemoryType;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class BotMoveTaskListener implements Listener {

    private final BotMoveTask task;

    public BotMoveTaskListener(BotMoveTask task) {
        this.task = task;
    }

    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent event) {
        if (event.getNPC().getId() != task.getBot().getNPC().getId()) return;

        BotLogger.debug(task.getIcon(), true,
                task.getBot().getId() + " ✅ Навигация завершена, ID таски: " + task.getUUID());

        BotPosition pos = task.getParams().getTarget();

        // Валидация — для отладки и логов
        boolean arrived = task.getBot().getNPC().getStoredLocation().getBlockX() == pos.getX()
                && task.getBot().getNPC().getStoredLocation().getBlockZ() == pos.getZ();

        if (!arrived) {
            BotLogger.debug(task.getIcon(), true,
                    task.getBot().getId() + " ⚠️ Навигатор завершил, но NPC не в точке XZ. Завершаем всё равно.");
        }
        
        Block block = BotWorldHelper.botPositionToWorldBlock(pos);
        if(block!=null) {
            BotBlockData data = new BotBlockData();
            data.setX(block.getX());
            data.setY(block.getY());
            data.setZ(block.getZ());
            data.setType(block.getType().toString());
            task.getBot().getBrain().getMemory().memorize(data, MemoryType.VISITED_BLOCKS);
        }
        task.stop();
    }

    @EventHandler
    public void onNavigationCancel(NavigationCancelEvent event) {
        if (event.getNPC().getId() != task.getBot().getNPC().getId()) return;

        BotLogger.debug(task.getIcon(), true,
                task.getBot().getId() + " ❌ Навигация отменена — NPC не смог дойти");
        task.stop();
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
