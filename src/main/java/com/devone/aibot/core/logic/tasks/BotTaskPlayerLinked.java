package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.entity.Player;

public abstract class BotTaskPlayerLinked implements BotTask {
    protected final Bot bot;
    protected final Player player;
    protected boolean isDone = false;
    protected boolean isPaused = false;
    protected String name;

    public BotTaskPlayerLinked(Bot bot, Player player, String name) {
        this.bot = bot;
        this.player = player;
        this.name = name;
    }

    @Override
    public void update() {

        BotLogger.info("✨ " + bot.getId() + " Running task: " + name);

        if (this.player!=null && !isPlayerOnline()) {
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
        BotLogger.info("🚨 Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в автономный режим.");
        bot.getLifeCycle().getTaskStackManager().clearTasks();
        bot.getLifeCycle().getTaskStackManager().pushTask(new BotTaskIdle(bot));
        isDone = true;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (isPaused) {
            BotLogger.info("⏳ " + bot.getId() + " Pausing...");
        } else {
            BotLogger.info("▶️ " + bot.getId() + " Resuming...");
        }
    }
}
