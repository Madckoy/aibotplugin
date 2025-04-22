package com.devone.bot.plugin.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReloadPluginCommand implements CommandExecutor {
    private final AIBotPlugin plugin;

    public BotReloadPluginCommand(AIBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || sender.hasPermission("aibotplugin.admin.reload")) {
            BotLogger.debug("♻️ ", true,"Перезагрузка AI Bot Plugin...");

            plugin.reloadPlugin(); // ✅ Вызываем метод перезагрузки

            sender.sendMessage("§aAI Bot Plugin успешно перезагружен!");
            return true;
        } else {
            sender.sendMessage("§cУ вас нет прав на выполнение этой команды.");
            return false;
        }
    }
}
