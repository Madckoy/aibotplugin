package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotFollowTask;
import com.devone.aibot.utils.BotLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotFollow implements CommandExecutor {

    private final BotManager botManager;

    public BotFollow(BotManager botManager) {
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

        BotLogger.debug("📌 /bot-follow: Бот " + bot.getId() + " следует за " + player.getName());

        // ✅ Добавляем задачу на следование
        BotFollowTask followTask = new BotFollowTask(bot, player);
        bot.getLifeCycle().getTaskStackManager().pushTask(followTask);

        player.sendMessage("§aБот " + bot.getId() + " теперь следует за вами!");

        return true;
    }
}
