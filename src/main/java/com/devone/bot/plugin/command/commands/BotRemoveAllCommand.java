package com.devone.bot.plugin.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;

import java.util.Collection;

public class BotRemoveAllCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotRemoveAllCommand(BotManager botManager) {
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

        botManager.removeAllBots();

        return true;
    }
}
