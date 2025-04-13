package com.devone.bot.core.command;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.logic.task.move.BotMoveTask;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;

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
        BotMoveTaskParams moveTaskParams = new BotMoveTaskParams();
        moveTaskParams.setTarget(new BotCoordinate3D(x, y, z)); 
        moveTask.configure(moveTaskParams);
        bot.addTaskToQueue(moveTask);

        BotLogger.info(true, "📌 /bot-move: Бот " + bot.getId() + " направляется в " + targetLocation);
        
        sender.sendMessage("✅ Бот '" + botName + "' направляется в " + x + " " + y + " " + z);
        
        return true; 

    }

}
