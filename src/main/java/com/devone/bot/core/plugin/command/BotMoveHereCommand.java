package com.devone.bot.core.plugin.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.logic.task.move.BotMoveTask;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.utils.logger.BotLogger;

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

        Location targetLocation = player.getLocation();

        BotLogger.info("📌 ", true,"/bot-move-here: Бот " + bot.getId() + " Идет к игроку в точкe " + targetLocation);


        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();

        // ✅ Добавляем задачу на перемещение
        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();
        moveTaskParams.setTarget(new BotLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ()));
        moveTask.setParams(moveTaskParams);
        bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);

        player.sendMessage("§aБот " + bot.getId() + " Идет к игроку!");

        return true;
    }

}
