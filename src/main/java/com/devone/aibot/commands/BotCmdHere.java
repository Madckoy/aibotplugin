package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotTaskMove;
import com.devone.aibot.utils.BotLogger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotCmdHere implements CommandExecutor {

    private final BotManager botManager;

    public BotCmdHere(BotManager botManager) {
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

        BotLogger.info("📌 /bot-here: Бот " + bot.getId() + " идет к " + formatLocation(targetLocation));

        // ✅ Добавляем задачу на перемещение
        BotTaskMove moveTask = new BotTaskMove(bot);
        moveTask.configure(targetLocation);
        bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);

        player.sendMessage("§aБот " + bot.getId() + " идет к вам!");

        return true;
    }

    private String formatLocation(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }
}
