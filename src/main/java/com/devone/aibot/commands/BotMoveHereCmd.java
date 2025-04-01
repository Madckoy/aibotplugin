package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotMoveTask;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotMoveHereCmd implements CommandExecutor {

    private final BotManager botManager;

    public BotMoveHereCmd(BotManager botManager) {
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

        Location targetLocation = player.getLocation();

        BotLogger.info(true,"📌 /bot-move-here: Бот " + bot.getId() + " Идет к игроку в точкe " + BotStringUtils.formatLocation(targetLocation));


        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();

        // ✅ Добавляем задачу на перемещение
        BotMoveTask moveTask = new BotMoveTask(bot);
        moveTask.configure(targetLocation);
        bot.addTaskToQueue(moveTask);

        player.sendMessage("§aБот " + bot.getId() + " Идет к игроку!");

        return true;
    }

}
