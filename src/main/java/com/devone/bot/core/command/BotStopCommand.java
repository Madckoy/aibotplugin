package com.devone.bot.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.logic.task.decision.BotDecisionMakeTask;

public class BotStopCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotStopCommand(BotManager botManager) {
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

        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();

        // ✅ Добавляем задачу на ожидание 5 минут
        BotDecisionMakeTask idleTask = new BotDecisionMakeTask(bot);
        bot.getLifeCycle().getTaskStackManager().pushTask(idleTask);

        player.sendMessage("§aБот " + bot.getId() + " Остановился и ждет!");

        return true;
    }
}
