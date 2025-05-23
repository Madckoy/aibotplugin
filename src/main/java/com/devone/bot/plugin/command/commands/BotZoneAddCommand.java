package com.devone.bot.plugin.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.utils.zone.BotZoneManager;

public class BotZoneAddCommand implements CommandExecutor {
    private final BotZoneManager zoneManager;

    public BotZoneAddCommand(BotZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 2) {
            player.sendMessage("§cUsage: /zone-add <radius> <zone_name>");
            return true;
        }

        try {
            int radius = Integer.parseInt(args[0]);
            String zoneName = args[1];
            Location loc = player.getLocation();
            zoneManager.addZone(zoneName, loc, radius);
            player.sendMessage("§aZone '" + zoneName + "' added with radius " + radius);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid radius. Use a number.");
        }
        return true;
    }
}
