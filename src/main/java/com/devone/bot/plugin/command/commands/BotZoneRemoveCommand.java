package com.devone.bot.plugin.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.utils.zone.BotZoneManager;

public class BotZoneRemoveCommand implements CommandExecutor {
    private final BotZoneManager zoneManager;

    public BotZoneRemoveCommand(BotZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /zone-remove <zone_name>");
            return true;
        }

        String zoneName = args[0];
        if (zoneManager.removeZone(zoneName)) {
            sender.sendMessage("§aZone '" + zoneName + "' removed.");
        } else {
            sender.sendMessage("§cZone '" + zoneName + "' not found.");
        }
        return true;
    }
}
