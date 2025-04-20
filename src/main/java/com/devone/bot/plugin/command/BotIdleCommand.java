package com.devone.bot.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.bot.task.reactive.container.BotIdleReactiveContainer;
import com.devone.bot.core.utils.BotUtils;

public class BotIdleCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotIdleCommand(BotManager botManager) {
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
        BotUtils.clearTasks(bot);

        bot.pushReactiveTask(new BotIdleReactiveContainer(bot));

        player.sendMessage("§aБот " + bot.getId() + " остановлен и переходит в режим ожидания.");

        return true;
    }
}
