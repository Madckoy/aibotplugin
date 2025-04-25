package com.devone.bot.plugin.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.task.reactive.container.BotReactiveEmptyContainer;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotMoveHereCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotMoveHereCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Bot bot = botManager.getOrSelectBot(player.getUniqueId());

        if (bot == null) {
            player.sendMessage("§cБот не найден.");
            return true;
        }

        Location playerLoc = player.getLocation();
        Vector dirBackwards = playerLoc.getDirection().normalize().multiply(-15); // 👈 15 блоков за спиной
        Location teleportLocation = playerLoc.clone().add(dirBackwards);

        BotPosition tpLoc = new BotPosition(
                teleportLocation.getBlockX(),
                teleportLocation.getBlockY(),
                teleportLocation.getBlockZ());

        BotPosition moveTo = new BotPosition(
                playerLoc.getBlockX(),
                playerLoc.getBlockY(),
                playerLoc.getBlockZ());

        BotLogger.debug("🥾", true,
                "/bot-move-here: Бот " + bot.getId() + " телепортируется и направляется к игроку " + moveTo);

        // 📦 Контейнер
        BotReactiveEmptyContainer cont = new BotReactiveEmptyContainer(bot);

        // 1. Телепорт за спину
        BotTeleportTask tp = new BotTeleportTask(bot, player);
        BotTeleportTaskParams tpParams = new BotTeleportTaskParams();
        tpParams.setPosition(tpLoc);
        tp.setParams(tpParams);
        tp.setObjective("Появление за спиной игрока");
        tp.setIcon("⚡");
        cont.add(tp);

        // 2. Движение к игроку
        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveParams = new BotMoveTaskParams();
        moveParams.setTarget(moveTo);
        moveTask.setParams(moveParams);
        moveTask.setObjective("Идём к игроку");
        moveTask.setIcon("🥾");
        cont.add(moveTask);

        // ⏯ Старт как реактивная цепочка
        BotTaskManager.push(bot, cont);

        player.sendMessage("§aБот " + bot.getId() + " телепортируется и идёт к вам!");

        return true;
    }
}
