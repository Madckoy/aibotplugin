package com.devone.bot.plugin.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.reactive.container.BotCalibrateReactiveContainer;

public class BotCalibrateCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotCalibrateCommand(BotManager botManager) {
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

        // ✅ Реактивная остановка с контейнером
        //BotTaskManager.clear(bot);

        BotTaskManager.push(bot, new BotCalibrateReactiveContainer(bot));

        player.sendMessage("§aБот " + bot.getId() + " остановлен и переходит в режим калибровки");

        return true;
    }
}
