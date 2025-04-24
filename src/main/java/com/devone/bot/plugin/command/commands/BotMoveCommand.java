package com.devone.bot.plugin.command.commands;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.reactive.container.BotReactiveEmptyContainer;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotMoveCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotMoveCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        BotLogger.debug("🔧 ", true, "Получена команда от сервера: " + Arrays.toString(args));

        if (args.length < 4) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-move <bot_id> <x> <y> <z>");
            BotLogger.debug("❌", true, "Недостаточно аргументов для /bot-move");
            return false;
        }

        String botName = args[0];

        int x, y, z;

        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);

        } catch (NumberFormatException e) {

            sender.sendMessage("❌ Координаты должны быть целыми числами.");

            BotLogger.debug("❌ ", true, "Координаты должны быть целыми числами.");

            return false;
        }

        Bot bot = botManager.getBot(botName);

        if (bot == null) {
            sender.sendMessage("❌ Бот с именем " + botName + " не найден.");

            BotLogger.debug("❌", true, "Бот с именем " + botName + " не найден.");

            return false;
        }

        // BotTaskManager.clear(bot);
        
        // ✅ Добавляем задачу на перемещение
        Location targetLocation = new Location(bot.getNPCEntity().getWorld(), x, y, z);
        // создаем контейнер
        BotReactiveEmptyContainer container = new BotReactiveEmptyContainer(bot);
        BotMoveTask moveTask = new BotMoveTask(bot);
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();
        moveTaskParams.setTarget(new BotPosition(x, y, z));
        moveTask.setParams(moveTaskParams);
        container.add(moveTask);
        BotTaskManager.push(bot, container);

        BotLogger.debug("📌 ", true, "/bot-move: Бот " + bot.getId() + " направляется в " + targetLocation);
        
        sender.sendMessage("✅ Бот '" + botName + "' направляется в " + x + " " + y + " " + z);

        return true;

    }

}
