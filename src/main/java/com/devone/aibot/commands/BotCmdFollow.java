package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotTaskFollow;
import com.devone.aibot.utils.BotLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotCmdFollow implements CommandExecutor {

    private final BotManager botManager;

    public BotCmdFollow(BotManager botManager) {
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

        BotLogger.info("📌 /bot-follow: Бот " + bot.getId() + " следует за " + player.getName());

        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();
        // ✅ Добавляем задачу на следование
        BotTaskFollow followTask = new BotTaskFollow(bot, player);
        bot.addTaskToQueue(followTask);

        player.sendMessage("§aБот " + bot.getId() + " теперь следует за вами!");

        return true;
    }
}
