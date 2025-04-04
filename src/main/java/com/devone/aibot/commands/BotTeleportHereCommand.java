package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;

import com.devone.aibot.core.logic.tasks.BotTeleportTask;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
