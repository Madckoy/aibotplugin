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
        registerCommand("bot-reload-plugin", new BotReloadPluginCmd(plugin));

        registerCommand("bot-add", new BotAddCmd(botManager));
        registerCommand("bot-select", new BotSelectCmd(botManager));
        registerCommand("bot-unselect", new BotUnselectCmd(botManager));
        registerCommand("bot-list", new BotListCmd(botManager));
        registerCommand("bot-remove", new BotRemoveCmd(botManager));
        registerCommand("bot-remove-all", new BotRemoveAllCmd(botManager));

        registerCommand("bot-here", new BotHereCmd(botManager));
        registerCommand("bot-tp", new BotTpCmd(botManager));
        registerCommand("bot-tp-here", new BotTpHereCmd(botManager));
        registerCommand("bot-move", new BotMoveCmd(botManager));
        registerCommand("bot-stop", new BotStopCmd(botManager));
        registerCommand("bot-follow", new BotFollowCmd(botManager));
        registerCommand("bot-protect", new BotProtectCmd(botManager));
        registerCommand("bot-cancel", new BotCancelCmd(botManager));

        registerCommand("zone-add", new BotZoneAddCmd(zoneManager));
        registerCommand("zone-remove", new BotZoneRemoveCmd(zoneManager));
        registerCommand("zone-list", new BotZoneListCmd(zoneManager));

        BotLogger.info(true, "✅ Все команды зарегистрированы: " + commandExecutors.keySet());
    }

    // ✅ Регистрируем команду и проверяем, существует ли она
    private void registerCommand(String command, CommandExecutor executor) {
        BotLogger.info(true, "🔹 Регистрация команды: " + command);

        if (plugin.getCommand(command) == null) {
            BotLogger.info(true, "❌ Ошибка: команда " + command + " не найдена в plugin.yml!");
            return;
        }

        commandExecutors.put(command.toLowerCase(), executor);
        plugin.getCommand(command).setExecutor(this);

        BotLogger.info(true, "✅ Команда зарегистрирована: " + command);
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
        BotLogger.info(true, "♻️ Команды перезагружены!");
    }
}
