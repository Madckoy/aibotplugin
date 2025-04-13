package com.devone.bot.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.chat.BotChat;

public class BotUnselectCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotUnselectCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            BotChat.sendMessageToPlayer((Player)sender, null, "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        if (!botManager.unselectBot(player.getUniqueId())) {
            BotChat.sendMessageToPlayer(player, null, "Бот больше не выбран.");
            return true;
        }
        
        return true;
    }
}
