package com.devone.bot.core.bot.behaviour.task.move.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.devone.bot.core.bot.behaviour.task.move.BotMoveTask;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;

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
        if (task.getBot().getNPC().getId() != event.getNPC().getId()) return;

        BotLogger.debug("✅", true, "Навигация завершена (BotMoveTaskListener), ID: " + task.getUUID());
        task.getBot().getState().setStuck(false);

        task.stop(); // Завершаем задачу движения
    }

    public void onNavigationCancel(NavigationCancelEvent event) {
        if(event.getNPC().getId() != task.getBot().getNPC().getId()) return;
        //task.getBot().getBrain().setStuck(true);
        BotLogger.debug("❌", true,"Навигация отменена (BotMoveTaskListener) — NPC не смог дойти");
        task.stop();
    }
    
public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
