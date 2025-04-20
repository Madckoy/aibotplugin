package com.devone.bot.plugin.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.task.active.move.BotMoveTask;
import com.devone.bot.core.bot.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.blocks.BotLocation;
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

        Location targetLocation = player.getLocation();
        BotLocation botLoc = new BotLocation(
            targetLocation.getBlockX(),
            targetLocation.getBlockY(),
            targetLocation.getBlockZ()
        );

        BotLogger.debug("🥾", true, "/bot-move-here: Бот " + bot.getId() + " идёт к игроку в точку " + botLoc);

        BotMoveTaskParams params = new BotMoveTaskParams();
        params.setTarget(botLoc);

        BotMoveTask moveTask = new BotMoveTask(bot);
        moveTask.setParams(params);
        moveTask.setObjective("Идём к игроку");
        moveTask.setIcon("🥾");

        bot.reactiveTaskStart(moveTask); // ✅ как реактивная задача

        player.sendMessage("§aБот " + bot.getId() + " направляется к вам!");

        return true;
    }
}
