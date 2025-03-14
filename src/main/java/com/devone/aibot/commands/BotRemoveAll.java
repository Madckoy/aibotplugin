package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BotRemoveAll implements CommandExecutor {

    private final BotManager botManager;

    public BotRemoveAll(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Collection<Bot> bots = botManager.getAllBots();

        if (bots.isEmpty()) {
            sender.sendMessage("§cNo active bots to remove.");
            return true;
        }

        botManager.despawnBots();

        return true;
    }
}
