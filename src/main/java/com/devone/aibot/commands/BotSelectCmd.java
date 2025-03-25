package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.comms.BotCommunicator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotSelectCmd implements CommandExecutor {

    private final BotManager botManager;

    public BotSelectCmd(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cUsage: /bot-select <bot_name>");
            return true;
        }

        String botName = args[0];
        Bot bot = botManager.getBot(botName); // Using BotManager to get the NPC by name

        if (bot == null) {
            player.sendMessage("§cBot '" + botName + "' not found.");
            return true;
        }

        // ✅ Store the selected bot in BotManager
        botManager.selectBot(player.getUniqueId(), bot);

        BotCommunicator.sendMessageToPlayer(player, botName, "You have selected bot " + botName + ".");

        // ✅ Bot responds with a modern chat message to confirm using Adventure API
        String botMessage = "I am now under your command!";

        BotCommunicator.sendMessageToPlayer(player, botName, botMessage);

        return true;
    }
}
