package com.devone.bot.plugin.command.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.container.BotReactiveMoveContainer;
import com.devone.bot.core.task.reactive.container.params.BotReactiveMoveContainerParams;
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
        // создаем контейнер
        BotReactiveMoveContainerParams params = new BotReactiveMoveContainerParams();
        params.position = new BotPosition(x, y, z);
        BotReactiveMoveContainer container = new BotReactiveMoveContainer(bot);
        container.setParams(params);
        BotTaskManager.push(bot, container);

        BotLogger.debug("📌 ", true, "/bot-move: Бот " + bot.getId() + " направляется в " + params.position);
        
        sender.sendMessage("✅ Бот '" + botName + "' направляется в " + x + " " + y + " " + z);

        return true;

    }

}
