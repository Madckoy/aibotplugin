package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotRemove implements CommandExecutor {

    private final BotManager botManager;

    public BotRemove(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        String botName = "";
        Player player = (Player) sender;

        Bot bot = botManager.getOrSelectBot(player.getUniqueId());

        if (args.length > 1) {
            sender.sendMessage("§cUsage: /bot-remove <bot_name>");
            return true;
        }

        if(bot == null) {
            botName = args[0];
        }

        bot = botManager.getBot(botName);

        if (bot == null) {
            sender.sendMessage("§cBot '" + botName + "' not found.");
            return true;
        }

        botManager.removeBot(botName);

        sender.sendMessage("§aBot '" + botName + "' Has been removed.");

        return true;
    }
}
