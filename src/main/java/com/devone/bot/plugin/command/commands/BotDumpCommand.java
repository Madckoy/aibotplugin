package com.devone.bot.plugin.command.commands;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.brain.perseption.scene.BotSceneSaver;
import com.devone.bot.core.utils.BotConstants;

import com.devone.bot.core.utils.logger.BotLogger;

public class BotDumpCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotDumpCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        BotLogger.debug("🔧", AIBotPlugin.getInstance().isLogged(), "Получена команда от сервера: " + Arrays.toString(args));

        if (args.length < 1) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-dump <bot_id>");
            BotLogger.debug("❌", AIBotPlugin.getInstance().isLogged(), "Недостаточно аргументов для /bot-dump");
            return false;
        }

        String botName = args[0];

        Bot bot = botManager.getBot(botName);

        if (bot == null) {
            sender.sendMessage("❌ Бот с именем " + botName + " не найден.");

            BotLogger.debug("❌", AIBotPlugin.getInstance().isLogged(), "Бот с именем " + botName + " не найден.");

            return false;
        }

        try {
            BotSceneData sceneDataTagged = bot.getBrain().getSceneData().clone(false);
            String fileNameTagged = BotConstants.PLUGIN_PATH_TMP + bot.getId() + "_scene.tagged";
            BotSceneSaver.saveToJsonFile(fileNameTagged, sceneDataTagged);

            BotSceneData sceneDataRaw = bot.getBrain().getSceneData().clone(true);
            String fileNameRaw = BotConstants.PLUGIN_PATH_TMP + bot.getId() + "_scene.raw";
            BotSceneSaver.saveToJsonFile(fileNameRaw, sceneDataRaw);

            BotLogger.info("🧠", AIBotPlugin.getInstance().isLogged(), " ✅ Бот скинул данные о сцене на диск: " + fileNameRaw + " и " + fileNameTagged);
            
        } catch (IOException e) {
            BotLogger.debug("🧠", AIBotPlugin.getInstance().isLogged(), " ❌ Ошибка сброса данных на диск");
        }

        return true;
    }

}
