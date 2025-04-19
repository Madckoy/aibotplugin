package com.devone.bot.core.bot.task.passive;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.brain.BotBrainTask;
import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.bot.task.reactive.BotReactivityManager;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;
import java.util.UUID;

public abstract class BotTask<T extends BotTaskParams> implements IBotTask, Listener, IBotTaskParameterized<T> {

    protected T params;

    //configurable
    protected boolean enabled = true;
    protected boolean stopped = false;

    protected boolean logging = true;

    // runtime
    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();

    private boolean pause = false;
    
    public boolean isPause() {
        return pause;
    }

    protected boolean done = false;
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
        BotLogger.debug("🚩", this.isLogging(), icon +" : "+ bot.getId() + "  Set Objective: " + objctv);
    }

    public void update() {

        if (!enabled) return;
    
        BotLogger.debug("🚦", this.isLogging(), bot.getId() +" "+icon + " : " + bot.getId() + " Status: done=" + done + " | paused=" + isPause() +
                " 📍: " + bot.getNavigation().getLocation() +" | 🎯: " + bot.getNavigation().getTarget());
    
        if (isPause()) return;
    
        if (this.player != null && !isPlayerOnline()) {
            handlePlayerDisconnect();
            return;
        }
    
        if (!bot.getBrain().isReactionInProgress()) {
            BotLogger.debug("🚨", this.isLogging(), bot.getId() + player.getName() + " Не Выполняет текущее реактивное задание.");
            
            Optional<Runnable> reaction = BotReactivityManager.checkReactions(bot);

            if (reaction.isPresent()) {

                setPause(true); // current task
                BotLogger.debug("🚨", this.isLogging(), bot.getId() + player.getName() + " Нужно срочно выполнить реактивное задание!");
                reaction.get().run();
                return;
            }
        }

        BotLogger.debug("🚨", this.isLogging(), bot.getId() + player.getName() + " Выполняет текущее задание.");
        execute();
    }    

    public Bot getBot() {
        return bot;
    }   

    public abstract void execute();

    public void stop() {
        done = true;
    }

    public String getUUID() {
        return uuid;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLogging() {
        return logging;
    }
    

    public void setPause(boolean pause) {
        this.pause = pause; 
        String status = this.pause ? this.icon + " ( "+this.getClass().getSimpleName()+" ) "+ " ⏸️ Pause" : " ▶️ Resume";
        BotLogger.debug(status, this.isLogging(), bot.getId());
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

        BotLogger.debug("🚨", this.isLogging(), "Игрок " + player.getName() + " вышел! Бот " + bot.getId() + " переходит в автономный режим.");
        
        BotUtils.clearTasks(bot);

        BotBrainTask task = new BotBrainTask(bot);

        BotUtils.pushTask(bot, task);

        this.stop();
    }

    public void turnToTarget(BotLocation target) {
        
        // ✅ Принудительно обновляем положение, если поворот сбрасывается
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {
            BotUtils.lookAt(bot, target);
        }, 1L); // ✅ Через тик, чтобы дать время на обновление

        BotLogger.debug("🔄", this.isLogging(), "TURNING: " + bot.getId() + " to look at the target: " + target);
    }

    public void animateHand() {
        if (bot.getNPCEntity() instanceof Player playerBot) {
            playerBot.swingMainHand();
            BotLogger.debug("✋🏻", this.isLogging(), "Анимация руки выполнена");
        } else {
            BotLogger.debug("✋🏻", this.isLogging(), "Анимация не выполнена: бот — не игрок");
        }
    }
}
