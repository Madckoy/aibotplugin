package com.devone.bot.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.speaker.BotSpeaker;

public class BotUnselectCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotUnselectCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            BotSpeaker.sendMessageToPlayer((Player)sender, null, "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        if (!botManager.unselectBot(player.getUniqueId())) {
            BotSpeaker.sendMessageToPlayer(player, null, "Бот больше не выбран.");
            return true;
        }
        
        return true;
    }
}
