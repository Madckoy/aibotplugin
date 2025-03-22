package com.devone.aibot.core.comms;

import org.bukkit.entity.Player;
import com.devone.aibot.core.Bot;

public class BotCommunicator {
    
    private final Bot bot;

    public BotCommunicator(Bot bot) {
        this.bot = bot;
    }

    // Метод для отправки сообщений игрокам
    public void sendMessageToPlayer(String message) {
        if (bot.getNPCEntity() instanceof Player) {
            Player player = (Player) bot.getNPCEntity();
            player.sendMessage("[Bot] " + message);
        } else {
            // Логика для других видов взаимодействий
            // Например, если бот не является игроком, можно общаться через консоль или другие средства.
        }
    }

    // Метод для отправки сообщений в глобальный чат или другие каналы
    public void broadcastMessage(String message) {
        // Допустим, если бот может взаимодействовать с миром или чатом
        bot.getRuntimeStatus().getCurrentLocation().getWorld().getPlayers().forEach(player -> {
            player.sendMessage("[Bot] " + message);
        });
    }

    // Другие возможные методы, например, для общения с другими ботами, игнорирование на определенные команды, и т.д.
}
