package com.devone.aibot.commands;

import com.devone.aibot.core.BotProtectedZone;
import com.devone.aibot.core.BotZoneManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotZoneListCommand implements CommandExecutor {
    private final BotZoneManager zoneManager;

    public BotZoneListCommand(BotZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (zoneManager.listZones().isEmpty()) {
            sender.sendMessage("§cNo protected zones.");
        } else {
            sender.sendMessage("§aProtected Zones:");
            for (String name : zoneManager.listZones()) {
                int radius = zoneManager.getZoneRadius(name);
                BotProtectedZone zone = zoneManager.getZone(name); // ✅ Get zone details
                if (zone != null) {
                    sender.sendMessage(" - " + name + " (Radius: " + radius + ") at [X: " +
                        (int) zone.getX() + ", Y: " + (int) zone.getY() + ", Z: " + (int) zone.getZ() + "]");
                }
            }
        }
        return true;
    }
}
