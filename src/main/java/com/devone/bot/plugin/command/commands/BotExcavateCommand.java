package com.devone.bot.plugin.command.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceContainerExcavate;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotExcavateCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotExcavateCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BotLogger.debug("🔧", AIBotPlugin.getInstance().isLogged(), "Получена команда от сервера: " + Arrays.toString(args));

        if (args.length < 1) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-excavate <bot_id>");
            BotLogger.debug("❌", AIBotPlugin.getInstance().isLogged(), "Недостаточно аргументов для /bot-excavate");
            return false;
        }

        String botName = args[0];

        Bot bot = botManager.getBot(botName);

        if (bot == null) {
            sender.sendMessage("❌ Бот с именем " + botName + " не найден.");

            BotLogger.debug("❌", AIBotPlugin.getInstance().isLogged(), "Бот с именем " + botName + " не найден.");

            return false;
        }

        BotTaskManager.push(bot, new BotSequenceContainerExcavate(bot));

        return true;
    }
}
