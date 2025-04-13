package com.devone.bot.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.logic.task.playerlinked.follow.BotFollowTargetTask;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotFollowCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotFollowCommand(BotManager botManager) {
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

        BotLogger.info(true,"📌 /bot-follow: Бот " + bot.getId() + " следует за " + player.getName());

        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();

        BotBlockData block_data = new BotBlockData();
        block_data.type = "player";
        block_data.x = player.getLocation().getBlockX();
        block_data.y = player.getLocation().getBlockY();
        block_data.z = player.getLocation().getBlockZ();
     
        
        // ✅ Добавляем задачу на следование
        BotFollowTargetTask followTask = new BotFollowTargetTask(bot, block_data);
        bot.addTaskToQueue(followTask);

        player.sendMessage("§aБот " + bot.getId() + " теперь следует за вами!");

        return true;
    }
}
