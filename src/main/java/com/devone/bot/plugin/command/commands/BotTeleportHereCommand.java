package com.devone.bot.plugin.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.container.BotReactiveTeleportToPlayerContainer;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotTeleportHereCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotTeleportHereCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Bot bot = botManager.getOrSelectBot(player.getUniqueId());

        if (bot == null) {
            player.sendMessage("§cБот не найден.");
            return true;
        }

        // Удаляем все задачи
        BotTaskManager.clear(bot);

        BotLogger.debug("📌", AIBotPlugin.getInstance().isLogged(), "/bot-tp-here: Бот " + bot.getId() + " будет телепортирован к игроку");

        BotTaskManager.push(bot, new BotReactiveTeleportToPlayerContainer(bot, player));

        player.sendMessage("§aБот " + bot.getId() + " телепортируется к вам!");

        return true;
    }
}
