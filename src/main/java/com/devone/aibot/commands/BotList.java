package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class BotList implements CommandExecutor {

    private final BotManager botManager;

    public BotList(BotManager botManager) {
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
            String locationText = (loc != null) ?
                "[X: " + (int) loc.getX() + ", Y: " + (int) loc.getY() + ", Z: " + (int) loc.getZ() + "]" :
                "[Location Unknown]";

            String goalText = (bot.getGoal() != null) ? bot.getGoal().name() : "NO_GOAL";
            String statusText = (bot.getStatus() != null) ? bot.getStatus().name() : "UNKNOWN";

            player.sendMessage(" - " + bot.getId() + " " + locationText + " " + goalText + " " + statusText);
        }

        if (botManager.getAllBots().isEmpty()) {
            player.sendMessage("§cNo bots have been created yet.");
        }

        return true;
    }
}
