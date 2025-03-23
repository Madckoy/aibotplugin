package com.devone.aibot.core.comms;

import org.bukkit.entity.Player;
import com.devone.aibot.core.Bot;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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

    public static void sendMessageToPlayer(Player to, String from, String message) {
        Component chatMessage;

        if (from == null) {
            // Сообщение от системы
            chatMessage = Component.text("[System] ", NamedTextColor.YELLOW)
                    .append(Component.text(message, NamedTextColor.WHITE));
        } else if (from.contains("Bot")) { 
            // Сообщение от бота (если в имени есть "Bot")
            chatMessage = Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text(from, NamedTextColor.AQUA)) // Имя бота – голубым
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .append(Component.text(message, NamedTextColor.WHITE));
        } else {
            // Сообщение от игрока или другого отправителя
            chatMessage = Component.text(from + ": ", NamedTextColor.GREEN)
                    .append(Component.text(message, NamedTextColor.WHITE));
        }

        to.sendMessage(chatMessage);
    }

    // Другие возможные методы, например, для общения с другими ботами, игнорирование на определенные команды, и т.д.
}
