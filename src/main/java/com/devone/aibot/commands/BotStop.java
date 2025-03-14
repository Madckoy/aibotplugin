package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotIdleTask;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotStop implements CommandExecutor {

    private final BotManager botManager;

    public BotStop(BotManager botManager) {
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
        // ✅ Добавляем задачу на ожидание 5 минут
        BotIdleTask idleTask = new BotIdleTask(bot);

        bot.getLifeCycle().getTaskStackManager().pushTask(idleTask);

        player.sendMessage("§aБот " + bot.getId() + " Остановился и ждет!");

        return true;
    }
}
