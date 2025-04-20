package com.devone.bot.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.task.active.drop.BotDropAllTask;
import com.devone.bot.core.bot.task.reactive.container.BotReactiveContainer;
import com.devone.bot.core.utils.BotUtils;

public class BotDropAllCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotDropAllCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-drop-all <bot_name>");
            return false;
        }

        String botName = args[0];
        Bot bot = botManager.getBot(botName);

        if (bot == null) {
            sender.sendMessage("❌ Бот с именем " + botName + " не найден.");
            return false;
        }

        BotReactiveContainer cont = new BotReactiveContainer(bot);
        // ✅ Добавляем задачу на drop
        BotDropAllTask task = new BotDropAllTask(bot, null);
        cont.add(task);
        BotUtils.pushReactiveTask(bot, cont);
        // Подтверждение для игрока
        sender.sendMessage("✅ Инвентарь бота '" + botName + "' был сброшен на месте.");

        return true;
    }
}
