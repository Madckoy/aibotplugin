package com.devone.aibot.commands;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotMoveTask;
import com.devone.aibot.utils.BotLogger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotMove implements CommandExecutor {

    private final BotManager botManager;

    public BotMove(BotManager botManager) {
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

        if (args.length != 3) {
            player.sendMessage("§cИспользование: /bot-move <x> <y> <z>");
            return true;
        }

        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);

            World world = player.getWorld();
            Location targetLocation = new Location(world, x, y, z);

            BotLogger.debug("📌 /bot-move: Бот " + bot.getId() + " идет к " + formatLocation(targetLocation));


            // ✅ Добавляем задачу на перемещение
            BotMoveTask moveTask = new BotMoveTask(bot);
            moveTask.configure(targetLocation);
            bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);

            player.sendMessage("§aБот " + bot.getId() + " идет к " + formatLocation(targetLocation));

        } catch (NumberFormatException e) {
            player.sendMessage("§cОшибка: координаты должны быть числами.");
        }

        return true;
    }

    private String formatLocation(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }
}
