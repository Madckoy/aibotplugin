package com.devone.bot.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.chat.BotChat;

public class PlayerListener implements Listener {

    private final BotManager botManager;

    public PlayerListener(BotManager botManager) {
        this.botManager = botManager;
    }

    /**
     * Обработчик выхода игрока
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        botManager.unselectBot(player.getUniqueId()); // Сбрасываем выбор бота
    }

    /**
     * Обработчик входа игрока
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

		BotChat.sendMessageToPlayer(player, null, "Добро пожаловать, " + player.getName() + "!");
    
        // Можно добавить восстановление состояния, если нужно
		// ...
    }
}
