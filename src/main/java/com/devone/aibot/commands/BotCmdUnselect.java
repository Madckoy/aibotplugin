package com.devone.aibot.commands;

import com.devone.aibot.core.BotManager;
import com.devone.aibot.utils.BotUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotCmdUnselect implements CommandExecutor {

    private final BotManager botManager;

    public BotCmdUnselect(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            BotUtils.sendMessageToPlayer((Player)sender, null, "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        if (!botManager.unselectBot(player.getUniqueId())) {
            BotUtils.sendMessageToPlayer(player, null, "Бот больше не выбран.");
            return true;
        }
        
        return true;
    }
}
