package com.devone.bot.core.logic.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.decision.BotDecisionMakeTask;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.logger.BotLogger;
import java.util.UUID;

public abstract class BotTask<T extends BotTaskParams> implements IBotTask, Listener, IBotTaskParameterized<T> {

    protected T params;

    //configurable
    protected boolean isEnabled = true;
    protected boolean isLogging = true;

    // runtime
    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();

    protected boolean isPaused = false;
    protected boolean isDone = false;
    protected final String uuid;
    
    //protected BotTaskParams params = new BotTaskParams(BotTaskParams.class.getSimpleName());
    protected String icon = "";
    protected String objective = "";

    protected boolean isListenerRegistered = false;

    public BotTask(Bot bot) {
        this.bot = bot;
        this.uuid = UUID.randomUUID().toString();
    }

    public BotTask(Bot bot, String icn) {
        this.bot = bot;
        this.icon = icn;
        this.uuid = UUID.randomUUID().toString();
    }

    public BotTask(Bot bot, Player player) {
        this.bot = bot;
        this.player = player;
        this.uuid = UUID.randomUUID().toString();
    }

    public BotTask(Bot bot, Player player, String icn) {
        this.bot = bot;
        this.player = player;
        this.icon = icn;
        this.uuid = UUID.randomUUID().toString();
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objctv) {
        objective = objctv;
        BotLogger.info("🚩", this.isLogging(), icon +" : "+ bot.getId() + "  Set Objective: " + objctv);
    }

    public void update() {
        if (isEnabled) {

            BotLogger.info("🚦", this.isLogging(), icon +" : "+ bot.getId() + " Status: " + isDone + " | " + isPaused +
                    " 📍 xyz: " + bot.getRuntimeStatus().getCurrentLocation() + " | " + 
                    " 🎯 xyz: " + bot.getRuntimeStatus().getTargetLocation());

        if (isPaused) return;

        if (this.player != null && !isPlayerOnline()) {
            handlePlayerDisconnect();
        }

        execute();

        }
    }

    public Bot getBot() {
        return bot;
    }   

    public abstract void execute();

    public void stop() {
        isDone = true;
    }

    public String getUUID() {
        return uuid;
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isLogging() {
        return isLogging;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        String status = isPaused ? "⏸️ Pausing..." : "▶️ Resuming...";
        BotLogger.info(status, this.isLogging(), bot.getId());
    }

    @Override
    public IBotTaskParameterized<T> setParams(T params) {
        this.params = params;
        this.startTime = System.currentTimeMillis();
        return this;
    }
    
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icn) {
        this.icon = icn;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    private boolean isPlayerOnline() {
        return player.isOnline();
    }

    private void handlePlayerDisconnect() {

        BotLogger.info("🚨", this.isLogging(), "Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в автономный режим.");
        this.bot.getLifeCycle().getTaskStackManager().clearTasks();

        BotDecisionMakeTask task = new BotDecisionMakeTask(bot);

        bot.getLifeCycle().getTaskStackManager().pushTask(task);

        this.stop();
    }

    public void turnToTarget(BotLocation target) {
        
        // ✅ Принудительно обновляем положение, если поворот сбрасывается
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {
            BotUtils.lookAt(bot, target);
        }, 1L); // ✅ Через тик, чтобы дать время на обновление

        BotLogger.info("🔄", this.isLogging(), "TURNING: " + bot.getId() + " to look at the target: " + target);
    }

    public void animateHand() {
        if (bot.getNPCEntity() instanceof Player playerBot) {
            playerBot.swingMainHand();
            BotLogger.info("✋🏻", this.isLogging(), "Анимация руки выполнена");
        } else {
            BotLogger.info("✋🏻", this.isLogging(), "Анимация не выполнена: бот — не игрок");
        }
    }
}
