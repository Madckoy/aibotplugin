package com.devone.bot.commands;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.logic.tasks.BotTeleportTask;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotStringUtils;


public class BotTeleportCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotTeleportCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        BotLogger.info(true, "🔧 Получена команда от сервера: " + Arrays.toString(args));

        if (args.length < 4) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-tp <bot_id> <x> <y> <z>");
            BotLogger.info(true, "❌ Недостаточно аргументов для /bot-tp");
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

            BotLogger.info(true, "❌ Координаты должны быть целыми числами.");
            
            return false;
        }

        Bot bot = botManager.getBot(botName);
        
        if (bot == null) {
            sender.sendMessage("❌ Бот с именем " + botName + " не найден.");
            
            BotLogger.info(true, "❌ Бот с именем " + botName + " не найден.");

            return false;
        }

        bot.getLifeCycle().getTaskStackManager().clearTasks();

        Location tpLoc = new Location(bot.getNPCEntity().getWorld(), x, y, z);
        BotTeleportTask task = new BotTeleportTask(bot, null);
        task.configure(tpLoc);
        bot.addTaskToQueue(task);

        BotLogger.info(true, "📌 /bot-tp: Бот " + bot.getId() + " телепортируется в " + BotStringUtils.formatLocation(tpLoc));
        
        sender.sendMessage("✅ Бот '" + botName + "' телепортируется в " + x + " " + y + " " + z);

        BotLogger.info(true,"✅ Бот '" + botName + "' телепортируется в " + x + " " + y + " " + z);
        
        return true;
    }

}
