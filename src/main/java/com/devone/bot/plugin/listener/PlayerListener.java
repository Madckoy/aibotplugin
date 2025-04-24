package com.devone.bot.plugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.devone.bot.core.BotManager;
import com.devone.bot.core.speaker.BotSpeaker;

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

		BotSpeaker.sendMessageToPlayer(player, null, "Добро пожаловать, " + player.getName() + "!");
    
        // Можно добавить восстановление состояния, если нужно
		// ...
    }
}
