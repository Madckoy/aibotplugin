package com.devone.bot.plugin.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.playerlinked.protect.BotProtectTask;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotProtectCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotProtectCommand(BotManager botManager) {
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

        BotLogger.debug("🛡️ ", true, "/bot-protect: Бот " + bot.getId() + " защищает " + player.getName());

        // ✅ Очищаем стек задач
        //BotTaskManager.clear(bot);

        // ✅ Добавляем задачу на защиту
        BotProtectTask protectTask = new BotProtectTask(bot, player);
        BotTaskManager.push(bot, protectTask);

        player.sendMessage("§aБот " + bot.getId() + " теперь защищает вас!");

        return true;
    }
}
