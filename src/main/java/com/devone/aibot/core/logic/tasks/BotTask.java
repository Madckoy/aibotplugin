package com.devone.aibot.core.logic.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import java.util.UUID; // Добавляем для генерации уникального ID


public abstract class BotTask implements IBotTask{

    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();
    protected String name = "☑️";
    protected boolean isPaused  = false;
    protected boolean isDone = false;
    protected Location targetLocation;
    protected boolean isEnabled = true;
    protected final String uuid; // 🆕 Уникальный ID задачи

    public BotTask(Bot bot) {
        this.bot = bot;
        this.uuid = UUID.randomUUID().toString(); // 🆕 Генерируем ID
    }

    public BotTask(Bot bot, String name) {
        this.bot = bot;
        this.name = name;
        this.uuid = UUID.randomUUID().toString(); // 🆕 Генерируем ID
    }

    public BotTask(Bot bot, Player player, String name) {
        this.bot = bot;
        this.player = player;
        this.name = name;
        this.uuid = UUID.randomUUID().toString(); // 🆕 Генерируем ID
    }

    @Override
    public void update() {

        //BotLogger.debug("✨ " + bot.getId() + " Running task: " + name + " [ID: " + uuid + "]");

        BotLogger.debug("🚦 " + bot.getId() + " " + name +" Status: "+ isDone +" | " +isPaused +
        " 📍 xyz: " +BotStringUtils.formatLocation(bot.getNPCCurrentLocation())+
        " 🎯 xyz: " +BotStringUtils.formatLocation(targetLocation) + " [ID: " + uuid + "]");
        
        if (isPaused) return;

        if (this.player!=null && !isPlayerOnline()) {
            handlePlayerDisconnect();
        }

        if( isEnabled ) { executeTask(); }
    }

    public abstract void executeTask();

    public String getUUID() {
        return uuid;
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
        String status = isPaused ? "⏸️ Pausing..." : "▶️ Resuming...";
        BotLogger.debug(status + bot.getId() + " [ID: " + uuid + "]");
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

    public void handleStuck() {
            if( targetLocation!= null ) {
                if(bot.getNPCEntity()!=null) {
                    BotLogger.trace("✨ " + bot.getId() + " Застрял! Телепортируемся в "+BotStringUtils.formatLocation(targetLocation));
                    
                    BotTaskTeleport tp = new BotTaskTeleport(bot, player);
                    tp.configure(targetLocation);

                    bot.addTaskToQueue(tp);
                }
                else {
                    // ??? уничтожать бота?
                    BotLogger.error("✨ " + bot.getId() + " Застрял! Нет Taget Location и нет NPC Entity!");
                }
            } else {
                if(bot.getNPCEntity()!=null) {
                  
                    BotLogger.trace("✨ " + bot.getId() + " Застрял! Нет Taget Location! Телепортируемся в точку респавна!");

                    BotTaskTeleport tp = new BotTaskTeleport(bot, player);
                    tp.configure(Bot.getFallbackLocation());
                    bot.addTaskToQueue(tp);

                } else {
                    // ??? уничтожать бота?
                    BotLogger.error("✨ " + bot.getId() + " Застрял! Нет Taget Location и нет NPC Entity!");
                }
            }
    }

    private boolean isPlayerOnline() {
        return player.isOnline();
    }

    private void handlePlayerDisconnect() {
        BotLogger.warn("🚨 Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в автономный режим.");
        this.bot.getLifeCycle().getTaskStackManager().clearTasks();

        bot.addTaskToQueue(new BotTaskIdle(bot));
        isDone = true;
    }

}
