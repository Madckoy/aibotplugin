package com.devone.aibot.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.commands.*;
import com.devone.aibot.utils.BotLogger;

import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> commandExecutors = new HashMap<>();

    public CommandDispatcher(AIBotPlugin plugin, BotManager botManager, ZoneManager zoneManager) {
        this.plugin = plugin;
        registerCommands(plugin, botManager, zoneManager);
    }

    // ✅ Регистрируем все команды
    private void registerCommands(AIBotPlugin plugin, BotManager botManager, ZoneManager zoneManager) {
        registerCommand("bot-reload-plugin", new BotReloadPlugin(plugin));

        registerCommand("bot-add", new BotAdd(botManager));
        registerCommand("bot-select", new BotSelect(botManager));
        registerCommand("bot-list", new BotList(botManager));
        registerCommand("bot-remove", new BotRemove(botManager));
        registerCommand("bot-remove-all", new BotRemoveAll(botManager));

        registerCommand("bot-here", new BotHere(botManager));
        registerCommand("bot-move", new BotMove(botManager));
        registerCommand("bot-stop", new BotStop(botManager));
        registerCommand("bot-follow", new BotFollow(botManager));
        registerCommand("bot-protect", new BotProtect(botManager));
        registerCommand("bot-cancel", new BotCancel(botManager));

        registerCommand("zone-add", new ZoneAdd(zoneManager));
        registerCommand("zone-remove", new ZoneRemove(zoneManager));
        registerCommand("zone-list", new ZoneList(zoneManager));

        BotLogger.debug("✅ Все команды зарегистрированы: " + commandExecutors.keySet());
    }

    // ✅ Регистрируем команду и проверяем, существует ли она
    private void registerCommand(String command, CommandExecutor executor) {
        BotLogger.debug("🔹 Регистрация команды: " + command);

        if (plugin.getCommand(command) == null) {
            BotLogger.debug("❌ Ошибка: команда " + command + " не найдена в plugin.yml!");
            return;
        }

        commandExecutors.put(command.toLowerCase(), executor);
        plugin.getCommand(command).setExecutor(this);

        BotLogger.debug("✅ Команда зарегистрирована: " + command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandExecutor executor = commandExecutors.get(command.getName().toLowerCase());

        if (executor != null) {
            return executor.onCommand(sender, command, label, args);
        }

        sender.sendMessage("§cUnknown command: " + command.getName());
        return false;
    }

    // ✅ Метод для перезагрузки команд без перезапуска плагина
    public void reloadCommands() {
        commandExecutors.clear();
        registerCommands((AIBotPlugin) plugin, ((AIBotPlugin) plugin).getBotManager(),
                ((AIBotPlugin) plugin).getZoneManager());
        BotLogger.debug("♻️ Команды перезагружены!");
    }
}
