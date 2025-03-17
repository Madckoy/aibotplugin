package com.devone.aibot.core.logic.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;


import java.util.UUID; // Добавляем для генерации уникального ID


public abstract class BotTask implements IBotTask{

    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();
    protected String name = "N/A";
    protected boolean isPaused  = false;
    protected boolean isDone = false;
    protected Location targetLocation;
    protected boolean isEnabled = true;
    protected final String taskId; // 🆕 Уникальный ID задачи

    public BotTask(Bot bot) {
        this.bot = bot;
        this.taskId = UUID.randomUUID().toString(); // 🆕 Генерируем ID
    }

    public BotTask(Bot bot, String name) {
        this.bot = bot;
        this.name = name;
        this.taskId = UUID.randomUUID().toString(); // 🆕 Генерируем ID
    }

    public BotTask(Bot bot, Player player, String name) {
        this.bot = bot;
        this.player = player;
        this.name = name;
        this.taskId = UUID.randomUUID().toString(); // 🆕 Генерируем ID
    }

    @Override
    public void update() {

        BotLogger.debug("✨ " + bot.getId() + " Running task: " + name + " [ID: " + taskId + "]");
        
        if (isPaused) return;

        if (this.player!=null && !isPlayerOnline()) {
            handlePlayerDisconnect();
            return;
        }

        if( isEnabled ) { executeTask(); }
    }

    public abstract void executeTask();

    public String getTaskId() {
        return taskId;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    public boolean isEnabled(){
        return isEnabled;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        String status = isPaused ? "⏳ Pausing..." : "▶️ Resuming...";
        BotLogger.debug(status + bot.getId());
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
        BotLogger.warn("🚨 Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в автономный режим.");
        this.bot.getLifeCycle().getTaskStackManager().clearTasks();
        this.bot.getLifeCycle().getTaskStackManager().pushTask( new BotTaskIdle(bot) );
        isDone = true;
    }

}
