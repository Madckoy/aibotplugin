package com.devone.bot.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.logic.tasks.teleport.BotTeleportTask;
import com.devone.bot.core.logic.tasks.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;

public class BotTeleportHereCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotTeleportHereCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Bot bot = botManager.getOrSelectBot(player.getUniqueId());

        if (bot == null) {
            player.sendMessage("§cБот не найден.");
            return true;
        }

        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();

        BotCoordinate3D targetLocation = new BotCoordinate3D(player.getLocation().getBlockX(),
                                                             player.getLocation().getBlockY(),
                                                             player.getLocation().getBlockZ()); 

        // ✅ Добавляем задачу на мгновенное перемещение
        BotTeleportTask task = new BotTeleportTask(bot, player);
        BotTeleportTaskParams taskParams = new BotTeleportTaskParams();
        taskParams.setTarget(targetLocation);
        task.configure(taskParams);
        
        bot.addTaskToQueue(task);

        BotLogger.info(true,"📌 /bot-tp-here: Бот " + bot.getId() + " Телепортируется в точку игрока" + 
        taskParams.getTarget().toString());

        player.sendMessage("§aБот " + bot.getId() + " Телепортируется к игроку!");

        return true;
    }

}
