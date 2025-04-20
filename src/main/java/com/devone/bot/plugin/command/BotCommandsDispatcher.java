package com.devone.bot.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.zone.BotZoneManager;

import java.util.HashMap;
import java.util.Map;

public class BotCommandsDispatcher implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> commandExecutors = new HashMap<>();

    public BotCommandsDispatcher(AIBotPlugin plugin, BotManager botManager, BotZoneManager zoneManager) {
        this.plugin = plugin;
        registerCommands(plugin, botManager, zoneManager);
    }

    // ✅ Регистрируем все команды
    private void registerCommands(AIBotPlugin plugin, BotManager botManager, BotZoneManager zoneManager) {
        registerCommand("bot-reload-plugin", new BotReloadPluginCommand(plugin));

        registerCommand("bot-add", new BotAddCommand(botManager));
        registerCommand("bot-select", new BotSelectCommand(botManager));
        registerCommand("bot-unselect", new BotUnselectCommand(botManager));

        registerCommand("bot-list", new BotListCommand(botManager));
        registerCommand("bot-remove", new BotRemoveCommand(botManager));
        registerCommand("bot-remove-all", new BotRemoveAllCommand(botManager));

        registerCommand("bot-tp", new BotTeleportCommand(botManager));
        registerCommand("bot-tp-here", new BotTeleportHereCommand(botManager));

        registerCommand("bot-move", new BotMoveCommand(botManager));
        registerCommand("bot-move-here", new BotMoveHereCommand(botManager));

        registerCommand("bot-stop", new BotIdleCommand(botManager));
        registerCommand("bot-chase", new BotChaseCommand(botManager));
        registerCommand("bot-protect", new BotProtectCommand(botManager));
        registerCommand("bot-idle", new BotIdleCommand(botManager));

        registerCommand("bot-drop-all", new BotDropAllCommand(botManager));

        registerCommand("zone-add", new BotZoneAddCommand(zoneManager));
        registerCommand("zone-remove", new BotZoneRemoveCommand(zoneManager));
        registerCommand("zone-list", new BotZoneListCommand(zoneManager));

        BotLogger.debug("✅", true, "Все команды зарегистрированы: " + commandExecutors.keySet());
    }

    // ✅ Регистрируем команду и проверяем, существует ли она
    private void registerCommand(String command, CommandExecutor executor) {
        BotLogger.debug("🔹", true, "Регистрация команды: " + command);

        if (plugin.getCommand(command) == null) {
            BotLogger.debug("❌", true, "Ошибка: команда " + command + " не найдена в plugin.yml!");
            return;
        }

        commandExecutors.put(command.toLowerCase(), executor);
        plugin.getCommand(command).setExecutor(this);

        BotLogger.debug("✅", true, "Команда зарегистрирована: " + command);
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
        BotLogger.debug("♻️", true, "Команды перезагружены!");
    }
}
