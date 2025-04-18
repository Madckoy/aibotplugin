package com.devone.bot.core.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.blocks.BotLocation;

public class BotListCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotListCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage("§aActive Bots:");

        for (Bot bot : botManager.getAllBots()) {
            BotLocation loc = bot.getNavigation().getLocation();
            player.sendMessage(bot.getId() + " " + loc );
        }

        if (botManager.getAllBots().isEmpty()) {
            player.sendMessage("§cNo bots have been created yet.");
        }

        return true;
    }
}
