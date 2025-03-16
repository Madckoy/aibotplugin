package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.entity.Player;

public abstract class PlayerLinkedTask implements BotTask {
    protected final Bot bot;
    protected final Player player;
    protected boolean isDone = false;
    protected String name;

    public PlayerLinkedTask(Bot bot, Player player, String name) {
        this.bot = bot;
        this.player = player;
        this.name = name;
    }

    @Override
    public void update() {
        BotLogger.info(bot.getId() + " Running task: " + name);
        if (!isPlayerOnline()) {
            handlePlayerDisconnect();
            return;
        }

        executeTask();
    }

    // ✅ Этот метод будет переопределяться в каждом подклассе
    protected abstract void executeTask();

    private boolean isPlayerOnline() {
        return player.isOnline();
    }

    private void handlePlayerDisconnect() {
        BotLogger.debug("🚨 Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в Idle.");
        bot.getLifeCycle().getTaskStackManager().clearTasks();
        bot.getLifeCycle().getTaskStackManager().pushTask(new BotIdleTask(bot));
        isDone = true;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) {
        // Не требуется
    }
}
