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

public class BotCmdDispatcher implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> commandExecutors = new HashMap<>();

    public BotCmdDispatcher(AIBotPlugin plugin, BotManager botManager, BotZoneManager zoneManager) {
        this.plugin = plugin;
        registerCommands(plugin, botManager, zoneManager);
    }

    // ✅ Регистрируем все команды
    private void registerCommands(AIBotPlugin plugin, BotManager botManager, BotZoneManager zoneManager) {
        registerCommand("bot-reload-plugin", new BotCmdReloadPlugin(plugin));

        registerCommand("bot-add", new BotCmdAdd(botManager));
        registerCommand("bot-select", new BotCmdSelect(botManager));
        registerCommand("bot-unselect", new BotCmdUnselect(botManager));
        registerCommand("bot-list", new BotCmdList(botManager));
        registerCommand("bot-remove", new BotCmdRemove(botManager));
        registerCommand("bot-remove-all", new BotCmdRemoveAll(botManager));

        registerCommand("bot-here", new BotCmdHere(botManager));
        registerCommand("bot-here-tp", new BotCmdHereTp(botManager));
        registerCommand("bot-move", new BotCmdMove(botManager));
        registerCommand("bot-stop", new BotCmdStop(botManager));
        registerCommand("bot-follow", new BotCmdFollow(botManager));
        registerCommand("bot-protect", new BotCmdProtect(botManager));
        registerCommand("bot-cancel", new BotCmdCancel(botManager));

        registerCommand("zone-add", new BotCmdZoneAdd(zoneManager));
        registerCommand("zone-remove", new BotCmdZoneRemove(zoneManager));
        registerCommand("zone-list", new BotCmdZoneList(zoneManager));

        BotLogger.info("✅ Все команды зарегистрированы: " + commandExecutors.keySet());
    }

    // ✅ Регистрируем команду и проверяем, существует ли она
    private void registerCommand(String command, CommandExecutor executor) {
        BotLogger.info("🔹 Регистрация команды: " + command);

        if (plugin.getCommand(command) == null) {
            BotLogger.info("❌ Ошибка: команда " + command + " не найдена в plugin.yml!");
            return;
        }

        commandExecutors.put(command.toLowerCase(), executor);
        plugin.getCommand(command).setExecutor(this);

        BotLogger.info("✅ Команда зарегистрирована: " + command);
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
        BotLogger.info("♻️ Команды перезагружены!");
    }
}
