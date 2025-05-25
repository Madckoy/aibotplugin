package com.devone.bot.plugin.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceContainerTeleportToPlayer;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceContainerTeleportToPlayerParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotMoveHereCommand implements CommandExecutor {

    private final BotManager botManager;

    public BotMoveHereCommand(BotManager botManager) {
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

        Location playerLoc = player.getLocation();

        BotPosition moveTo = new BotPosition(
                playerLoc.getBlockX(),
                playerLoc.getBlockY(),
                playerLoc.getBlockZ());

        BotLogger.debug("🥾", AIBotPlugin.getInstance().isLogged(),
                "/bot-move-here: Бот " + bot.getId() + " телепортируется и направляется к игроку " + moveTo);

        // 📦 Контейнер
        BotSequenceContainerTeleportToPlayerParams params = new BotSequenceContainerTeleportToPlayerParams();
        BotSequenceContainerTeleportToPlayer cont = new BotSequenceContainerTeleportToPlayer(bot, player);
        cont.setParams(params);
        // ⏯ Старт как реактивная цепочка
        BotTaskManager.push(bot, cont);

        player.sendMessage("§aБот " + bot.getId() + " телепортируется и идёт к вам!");

        return true;
    }
}
