package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.utils.BotStringUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class BotCmdList implements CommandExecutor {

    private final BotManager botManager;

    public BotCmdList(BotManager botManager) {
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
            Location loc = bot.getNPCCurrentLocation();
            String locationText = BotStringUtils.formatLocation(loc);
            player.sendMessage(bot.getId() + " " + locationText );
        }

        if (botManager.getAllBots().isEmpty()) {
            player.sendMessage("§cNo bots have been created yet.");
        }

        return true;
    }
}
