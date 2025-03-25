package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotProtectTask;
import com.devone.aibot.utils.BotLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotProtectCmd implements CommandExecutor {

    private final BotManager botManager;

    public BotProtectCmd(BotManager botManager) {
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

        BotLogger.info(true,"🛡️ /bot-protect: Бот " + bot.getId() + " защищает " + player.getName());

        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();

        // ✅ Добавляем задачу на защиту
        BotProtectTask protectTask = new BotProtectTask(bot, player);
        bot.addTaskToQueue(protectTask);

        player.sendMessage("§aБот " + bot.getId() + " теперь защищает вас!");

        return true;
    }
}
