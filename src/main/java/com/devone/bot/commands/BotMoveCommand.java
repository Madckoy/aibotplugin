package com.devone.bot.commands;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.logic.tasks.BotMoveTask;
import com.devone.bot.core.logic.tasks.BotTeleportTask;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotStringUtils;

public class BotMoveCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotMoveCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

       BotLogger.info(true, "🔧 Получена команда от сервера: " + Arrays.toString(args));

        if (args.length < 4) {
            sender.sendMessage("❌ Недостаточно аргументов. Используйте: /bot-move <bot_id> <x> <y> <z>");
            BotLogger.info(true, "❌ Недостаточно аргументов для /bot-move");
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

        Location targetLocation = new Location(bot.getNPCEntity().getWorld(), x, y, z);
        // ✅ Добавляем задачу на перемещение
        BotMoveTask moveTask = new BotMoveTask(bot);
        moveTask.configure(targetLocation);
        bot.addTaskToQueue(moveTask);

        BotLogger.info(true, "📌 /bot-move: Бот " + bot.getId() + " направляется в " + BotStringUtils.formatLocation(targetLocation));
        
        sender.sendMessage("✅ Бот '" + botName + "' направляется в " + x + " " + y + " " + z);
        
        return true; 

    }

}
