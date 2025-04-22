package com.devone.bot.plugin.command.commands;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.brain.memory.scene.BotSceneData;
import com.devone.bot.core.brain.memory.scene.BotSceneSaver;

import com.devone.bot.core.utils.BotConstants;

import com.devone.bot.core.utils.logger.BotLogger;

public class BotDumpCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotDumpCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        BotLogger.debug("🔧", true, "Получена команда от сервера: " + Arrays.toString(args));

        if (args.length < 1) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-dump <bot_id>");
            BotLogger.debug("❌", true, "Недостаточно аргументов для /bot-dump");
            return false;
        }

        String botName = args[0];

        Bot bot = botManager.getBot(botName);

        if (bot == null) {
            sender.sendMessage("❌ Бот с именем " + botName + " не найден.");

            BotLogger.debug("❌", true, "Бот с именем " + botName + " не найден.");

            return false;
        }

        BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();

        String fileName = BotConstants.PLUGIN_TMP + bot.getId() + "_scene_data.json";

        try {
            BotSceneSaver.saveToJsonFile(fileName, sceneData);
            BotLogger.debug("🧠", true, " ✅ Бот скинул данные о сцене на диск" + fileName);
        } catch (IOException e) {
            BotLogger.debug("🧠", true, " ❌ Ошибка сброса данных на диск");
        }

        return true;
    }

}
