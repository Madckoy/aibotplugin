package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.configs.BotTaskConfig;
import com.devone.bot.core.logic.tasks.decision.BotDecisionMakeTask;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.scene.BotSceneData;

import java.util.UUID;

public abstract class BotTask implements IBotTask, IBotTaskConfigurable, Listener {

    //configurable
    protected boolean isEnabled = true;
    protected boolean isLogged = true;
    // runtime
    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();
    protected String name = "☑️";
    protected boolean isPaused = false;
    protected boolean isDone = false;
    protected final String uuid;
    protected BotSceneData sceneData;
    protected String objective;
    
    protected BotTaskConfig config;
    protected boolean isListenerRegistered = false;

    public BotTask(Bot bot) {
        this.config = new BotTaskConfig(null);
        this.bot = bot;
        this.uuid = UUID.randomUUID().toString();
        objective = "";
    }

    public BotTask(Bot bot, String name) {
        this.bot = bot;
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
    }

    public BotTask(Bot bot, Player player, String name) {
        this.bot = bot;
        this.player = player;
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
    }

    public void setSceneData(BotSceneData sceneData) {
        this.sceneData = sceneData;
    }

    public BotSceneData getSceneData() {
        return this.sceneData;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objctv) {
        objective = objctv;
        BotLogger.info(this.isLogged(), "🚩 " + name +" : "+ bot.getId() + "  Set Objective: " + objctv);
    }

    @Override
    public void update() {

        BotLogger.info(this.isLogged(), "🚦 " + name +" : "+ bot.getId() + " " + name + " Status: " + isDone + " | " + isPaused +
                " 📍 xyz: " + bot.getRuntimeStatus().getCurrentLocation() + " | " + 
                " 🎯 xyz: " + bot.getRuntimeStatus().getTargetLocation());

        if (isPaused) return;

        if (this.player != null && !isPlayerOnline()) {
            handlePlayerDisconnect();
        }

        if (isEnabled) {
            execute();
        }
    }

    public Bot getBot() {
        return bot;
    }   

    public abstract void execute();

    public abstract void stop();

    public String getUUID() {
        return uuid;
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isLogged() {
        return isLogged;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        String status = isPaused ? "⏸️ Pausing..." : "▶️ Resuming...";
        BotLogger.info(this.isLogged(), status + bot.getId());
    }

    @Override
    public BotTask configure(IBotTaskParams params) {
        startTime = System.currentTimeMillis();
        return this;
    }

    public BotTaskConfig getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    private boolean isPlayerOnline() {
        return player.isOnline();
    }

    private void handlePlayerDisconnect() {
        BotLogger.info(this.isLogged(), "🚨 Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в автономный режим.");
        this.bot.getLifeCycle().getTaskStackManager().clearTasks();

        bot.addTaskToQueue(new BotDecisionMakeTask(bot));

        this.stop();
    }

    public void turnToTarget(BotCoordinate3D target) {
        
        // ✅ Принудительно обновляем положение, если поворот сбрасывается
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {
            BotUtils.lookAt(bot, target);
        }, 1L); // ✅ Через тик, чтобы дать время на обновление

        BotLogger.info(this.isLogged(), "🔄 TURNING: " + bot.getId() + " to look at the target: " + target);
    }

    public void animateHand() {
        if (bot.getNPCEntity() instanceof Player playerBot) {
            playerBot.swingMainHand();
            BotLogger.info(this.isLogged(), "✋🏻 Анимация руки выполнена");
        } else {
            BotLogger.info(this.isLogged(), "✋🏻 Анимация не выполнена: бот — не игрок");
        }
    }
}
