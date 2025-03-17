package com.devone.aibot.core.logic.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;

public abstract class BotTask implements IBotTask{

    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();
    protected String name = "N/A";
    protected boolean isPaused  = false;
    protected boolean isDone = false;
    protected Location targetLocation;

    public BotTask(Bot bot) {
        this.bot = bot;
    }

    public BotTask(Bot bot, String name) {
        this.bot = bot;
        this.name = name;
    }

    public BotTask(Bot bot, Player player, String name) {
        this.bot = bot;
        this.player = player;
        this.name = name;
    }

    @Override
    public void update() {
        BotLogger.info("✨ " + bot.getId() + " Running task: " + name);
        
        if (isPaused) return;

        if (this.player!=null && !isPlayerOnline()) {
            handlePlayerDisconnect();
            return;
        }

        executeTask();
    }

    public abstract void executeTask();

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        String status = isPaused ? "⏳ Pausing..." : "▶️ Resuming...";
        BotLogger.info(status + bot.getId());
    }

    @Override
    public void configure(Object... params) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public String getName() {
       return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public Location getTargetLocation() {
       return targetLocation;
    }

    public void setTargetLocation(Location loc) {
        this.targetLocation = loc;
     }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public boolean handleStuck() {
        if( targetLocation!= null ) {
            bot.getNPCEntity().teleport(targetLocation);
        } else {
            bot.getNPCEntity().teleport(Bot.getFallbackLocation());
        }
        return true; //do teleport maybe?
    }

    private boolean isPlayerOnline() {
        return player.isOnline();
    }

    private void handlePlayerDisconnect() {
        BotLogger.info("🚨 Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в автономный режим.");
        this.bot.getLifeCycle().getTaskStackManager().clearTasks();
        this.bot.getLifeCycle().getTaskStackManager().pushTask( new BotTaskIdle(bot) );
        isDone = true;
    }

}
