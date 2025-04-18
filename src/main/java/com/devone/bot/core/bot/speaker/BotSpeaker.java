package com.devone.bot.core.bot.speaker;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.utils.world.BotWorldHelper;


public class BotSpeaker {
    
    private final Bot bot;

    public BotSpeaker(Bot bot) {
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
    public static void broadcastMessage(String message) {
        // Допустим, если бот может взаимодействовать с миром или чатом
        BotWorldHelper.getWorld().getPlayers().forEach(player -> {
            player.sendMessage(message);
        });
    }

    public static void sendMessageToPlayer(Player to, String from, String message) {

        to.sendMessage(message);

    }

    // Другие возможные методы, например, для общения с другими ботами, игнорирование на определенные команды, и т.д.
}
