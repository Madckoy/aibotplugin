package com.devone.aibot.commands;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotReloadPluginCommand implements CommandExecutor {
    private final AIBotPlugin plugin;

    public BotReloadPluginCommand(AIBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || sender.hasPermission("aibotplugin.admin.reload")) {
            BotLogger.info(true,"♻️ Перезагрузка AI Bot Plugin...");

            plugin.reloadPlugin(); // ✅ Вызываем метод перезагрузки

            sender.sendMessage("§aAI Bot Plugin успешно перезагружен!");
            return true;
        } else {
            sender.sendMessage("§cУ вас нет прав на выполнение этой команды.");
            return false;
        }
    }
}
