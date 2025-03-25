package com.devone.aibot.commands;

import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.comms.BotCommunicator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotUnselectCmd implements CommandExecutor {

    private final BotManager botManager;

    public BotUnselectCmd(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            BotCommunicator.sendMessageToPlayer((Player)sender, null, "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        if (!botManager.unselectBot(player.getUniqueId())) {
            BotCommunicator.sendMessageToPlayer(player, null, "Бот больше не выбран.");
            return true;
        }
        
        return true;
    }
}
