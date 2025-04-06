package com.devone.bot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.logic.tasks.BotTeleportTask;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotStringUtils;

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

        // ✅ Добавляем задачу на мгновенное перемещение
        BotTeleportTask task = new BotTeleportTask(bot, player);
        task.configure(player.getLocation());
        bot.addTaskToQueue(task);

        BotLogger.info(true,"📌 /bot-tp-here: Бот " + bot.getId() + " Телепортируется в точку игрока" + 
                                                  BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));

        player.sendMessage("§aБот " + bot.getId() + " Телепортируется к игроку!");

        return true;
    }

}
