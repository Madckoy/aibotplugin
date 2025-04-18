package com.devone.bot.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.brain.logic.task.teleport.BotTeleportTask;
import com.devone.bot.core.bot.brain.logic.task.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;

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

        BotLocation targetLocation = new BotLocation(player.getLocation().getBlockX(),
                                                             player.getLocation().getBlockY(),
                                                             player.getLocation().getBlockZ()); 

        // ✅ Добавляем задачу на мгновенное перемещение
        BotTeleportTask task = new BotTeleportTask(bot, player);
        BotTeleportTaskParams taskParams = new BotTeleportTaskParams();
        taskParams.setLocation(targetLocation);
        task.setParams(taskParams);
        
        bot.getLifeCycle().getTaskStackManager().pushTask(task);

        BotLogger.debug("📌", true,"/bot-tp-here: Бот " + bot.getId() + " Телепортируется в точку игрока" + 
        taskParams.getLocation().toString());

        player.sendMessage("§aБот " + bot.getId() + " Телепортируется к игроку!");

        return true;
    }

}
