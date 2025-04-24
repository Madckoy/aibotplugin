package com.devone.bot.plugin.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.utils.blocks.BotPosition;

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
            BotPosition loc = bot.getNavigator().getPosition();
            player.sendMessage(bot.getId() + " " + loc );
        }

        if (botManager.getAllBots().isEmpty()) {
            player.sendMessage("§cNo bots have been created yet.");
        }

        return true;
    }
}
