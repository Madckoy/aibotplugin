package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotDropAllTask;
import com.devone.aibot.core.logic.tasks.BotMoveTask;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

        Location targetLocation = bot.getRuntimeStatus().getCurrentLocation();

        // ✅ Добавляем задачу на перемещение
        BotDropAllTask dropAllTask = new BotDropAllTask(bot, null);
        dropAllTask.configure(targetLocation);
        bot.addTaskToQueue(dropAllTask);

        // Подтверждение для игрока
        sender.sendMessage("✅ Инвентарь бота '" + botName + "' был сброшен на месте.");

        return true;
    }
}
