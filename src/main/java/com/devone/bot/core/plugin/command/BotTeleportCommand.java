package com.devone.bot.core.plugin.command;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.logic.task.teleport.BotTeleportTask;
import com.devone.bot.core.logic.task.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.logger.BotLogger;


public class BotTeleportCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotTeleportCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        BotLogger.debug("🔧", true, "Получена команда от сервера: " + Arrays.toString(args));

        if (args.length < 4) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-tp <bot_id> <x> <y> <z>");
            BotLogger.debug("❌", true, "Недостаточно аргументов для /bot-tp");
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

            BotLogger.debug("❌", true, "Координаты должны быть целыми числами.");
            
            return false;
        }

        Bot bot = botManager.getBot(botName);
        
        if (bot == null) {
            sender.sendMessage("❌ Бот с именем " + botName + " не найден.");
            
            BotLogger.debug("❌", true, "Бот с именем " + botName + " не найден.");

            return false;
        }

        bot.getLifeCycle().getTaskStackManager().clearTasks();


        BotTeleportTask task = new BotTeleportTask(bot, null);
        BotTeleportTaskParams tpParams = new BotTeleportTaskParams();
        tpParams.setLocation(new BotLocation(x, y, z));  
        task.setParams(tpParams);
        bot.getLifeCycle().getTaskStackManager().pushTask(task);

        BotLogger.debug("📌", true, "/bot-tp: Бот " + bot.getId() + " телепортируется в " + tpParams.getLocation());
        
        sender.sendMessage("✅ Бот '" + botName + "' телепортируется в " + x + " " + y + " " + z);

        BotLogger.debug("✅", true,"Бот '" + botName + "' телепортируется в " + x + " " + y + " " + z);
        
        return true;
    }

}
