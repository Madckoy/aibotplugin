package com.devone.bot.core.task.active.move.listeners;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.BotMoveTaskHelper;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BotMoveTaskListener implements Listener {

    private final BotMoveTask task;

    public BotMoveTaskListener(BotMoveTask task) {
        this.task = task;
    }

    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent event) {
        if (event.getNPC().getId() != task.getBot().getNPC().getId()) return;

        BotLogger.debug(task.getIcon(), task.isLogged(),
                task.getBot().getId() + " ✅ Навигация завершена, ID таски: " + task.getUUID());

        BotPosition pos = task.getBot().getNavigator().getTarget().getPosition();


        // Валидация — для отладки и логов
        boolean arrived = task.getBot().getNPC().getStoredLocation().getBlockX() == pos.getX()
                && task.getBot().getNPC().getStoredLocation().getBlockZ() == pos.getZ();

        if (!arrived) {
            BotLogger.debug(task.getIcon(), task.isLogged(),
                    task.getBot().getId() + " ⚠️ Навигатор завершил, но NPC не в точке XZ. Завершаем всё равно.");
                   
        }

        onComplete(task.getBot());

        Block block = BotWorldHelper.botPositionToWorldBlock(pos);
        if (block != null) {
            BotBlockData data = new BotBlockData(block.getX(), block.getY(), block.getZ());
            data.setType(block.getType().toString());

           BotMemoryV2Utils.memorizePositionAndTime(task.getBot(), pos);
        }

        task.stop();
    }

    @EventHandler
    public void onNavigationCancel(NavigationCancelEvent event) {
        if (event.getNPC().getId() != task.getBot().getNPC().getId()) return;

        BotLogger.debug(task.getIcon(), task.isLogged(),
                task.getBot().getId() + " ❌ Навигация отменена — NPC не смог дойти");
        task.stop();
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void onComplete(Bot bot) {

        BotPosition actual = bot.getNavigator().getPosition();
    
        if (actual != null) {

            BotPosition centered = BotMoveTaskHelper.centerBlock(actual);

            Location aligned = new Location(
                    BotWorldHelper.getBotWorld(bot),
                    centered.getX(),
                    centered.getY()+0.01,
                    centered.getZ()
                );

            aligned.setYaw(bot.getNavigator().getPosition().getYaw());
            aligned.setPitch(bot.getNavigator().getPosition().getPitch());

            // System.out.println(centered);    
            // System.out.println(aligned);    

            Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                        bot.getNPC().teleport(aligned, PlayerTeleportEvent.TeleportCause.PLUGIN);
                });

            BotLogger.debug("🧭", bot.isLogged(), bot.getId() + " 📌 Выровнен в центр блока через NavigationCompleteListener: " + bot.getNPC().getStoredLocation());
        }
    }
}
