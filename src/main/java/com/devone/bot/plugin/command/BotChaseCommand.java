package com.devone.bot.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.behaviour.task.playerlinked.chase.BotChaseTargetTask;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;

public class BotChaseCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotChaseCommand(BotManager botManager) {
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

        BotLogger.debug("📌", true,"/bot-chase: Бот " + bot.getId() + " следует за " + player.getName());

        // ✅ Очищаем стек задач
        bot.getLifeCycle().getTaskStackManager().clearTasks();

        BotBlockData block_data = new BotBlockData();
        block_data.setType("player");
        block_data.setX(player.getLocation().getBlockX());
        block_data.setY(player.getLocation().getBlockY());
        block_data.setZ(player.getLocation().getBlockZ());
     
        
        // ✅ Добавляем задачу на следование
        BotChaseTargetTask followTask = new BotChaseTargetTask(bot, block_data);

        bot.getLifeCycle().getTaskStackManager().pushTask(followTask);

        player.sendMessage("§aБот " + bot.getId() + " теперь следует за вами!");

        return true;
    }
}
