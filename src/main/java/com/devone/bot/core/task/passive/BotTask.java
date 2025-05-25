package com.devone.bot.core.task.passive;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.brain.BotBrainTask;
import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.task.reactive.BotReactiveUtils;
import com.devone.bot.core.task.reactive.BotReactivityManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;
import java.util.UUID;

public abstract class BotTask<T extends BotTaskParams> implements IBotTask, Listener, IBotTaskParameterized<T> {

    protected T params;

    protected boolean injected = false;

    public boolean isInjected() {
        return injected;
    }

    public void setInjected(boolean injected) {
        this.injected = injected;
    }

    public T getParams() {
        return params;
    }

    protected boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    protected boolean stopped = false;
    
    protected boolean isLogged = true;

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();

    private boolean pause = false;
    private boolean deffered = false;

    public boolean isDeffered() {
        return deffered;
    }

    public void setDeffered(boolean deffered) {
        this.deffered = deffered;
    }

    protected boolean done = false;

    protected final String uuid;
    protected String icon = "";
    protected String objective = "";

    protected boolean isListenerRegistered = false;
    private boolean isReactive = false;

    private long pauseStartTime=0;

    public BotTask(Bot bot) {
        this.bot = bot;
        this.uuid = UUID.randomUUID().toString();
        startTime = System.currentTimeMillis();
    }

    public BotTask(Bot bot, String icn) {
        this(bot);
        this.icon = icn;
    }

    public BotTask(Bot bot, Player player) {
        this(bot);
        this.player = player;
    }

    public BotTask(Bot bot, Player player, String icn) {
        this(bot, player);
        this.icon = icn;
    }

    public void update() {
        logTaskStatus();
    
        // ⛔ Если задача выключена - останавливаем её правильно
        if (!isEnabled()) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " 🛑 Задача выключена и будет остановлена: " + this.getClass().getSimpleName());
            stop();
            return;
        }
    
        if (isPause() && isDeffered()==false) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ⏸️ Задача на паузе: " + this.getClass().getSimpleName());

            if (isPauseTimedOut(BotConstants.DEFAULT_TASK_TIMEOUT)) { 
                BotLogger.debug("🤖", isLogged(), bot.getId() + " ⏳ Таймаут паузы. Убираем задачу.");
                stop();
                return;
            }
            return;
        }
    
        if (playerDisconnected()) {
            handlePlayerDisconnect();
            return;
        }
    
        if (handleReactiveLogic())
            return;
    
        if(injected==true) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ▶️ Задача не на паузе и в стеке. Выполняем: " + this.getClass().getSimpleName());
            runTaskExecution();
        }
    }    

    private void logTaskStatus() {
        String pos = "N/A";
        String poi = "N/A";

        if(bot.getNavigator().getPosition()!=null ) {
            pos = bot.getNavigator().getPosition().toCompactString();
        }

        if(bot.getNavigator().getTarget()!=null ) {
            poi = bot.getNavigator().getTarget().toCompactString();
        }

        BotLogger.debug(icon, isLogged(), bot.getId() +
                " ❓ Status: done: " + done +", enabled: "+isEnabled() +", paused: " + pause + " , deffered: " + deffered + ", " +
                " 📍: " + pos + " / 🎯: " + poi);
    }

    private boolean playerDisconnected() {
        return player != null && !player.isOnline();
    }

    private boolean handleReactiveLogic() {
        if (isReactive && !BotReactiveUtils.isAlreadyReacting(bot)) {
            BotLogger.debug("🧠", isLogged(),
                    bot.getId() + " ⚠️ Инжектаем реактивный контейнер с задачами (task = " + getClass().getSimpleName() + ")");

            BotReactiveUtils.activateReaction(bot, true);
        }

        if (!BotReactiveUtils.isAlreadyReacting(bot)) {
            Optional<Runnable> reaction = BotReactivityManager.checkReactions(bot);
            if (reaction.isPresent()) {
                setPause(true);
                BotLogger.debug("🧠", isLogged(), bot.getId() + " 🚨 Обнаружена реакция. Текущая задача приостановлена.");
                reaction.get().run();
                return true;
            }
        }

        return false;
    }

    private void runTaskExecution() {
        BotLogger.debug("🧠", isLogged(), bot.getId() + " 🟡 Выполнение: " + icon + " " + getClass().getSimpleName());
        execute();
    }

    public abstract void execute();

    public void stop() {
        done = true;

        if (isReactive && BotReactiveUtils.isReactionOwnedBy(bot, this)) {
            BotLogger.debug("🧠", isLogged(),
                    bot.getId() + " 🧹 Завершена реактивная задача: " + getClass().getSimpleName());
            BotReactiveUtils.activateReaction(bot, false);
        }
        // ✅ Снимаем паузу, если вдруг задача её не сняла сама
        if (isPause()) {
            setPause(false);
        }
    }

    public void setPause(boolean pause) {
        this.pause = pause;

        if (pause) {
            this.pauseStartTime = System.currentTimeMillis();
        } else {
            this.pauseStartTime = 0L;
        }

        String status = pause ? "⏸️ Pause" : "▶️ Resume";
        BotLogger.debug(icon, isLogged(), bot.getId() + " " + status + " (" + getClass().getSimpleName() + ")");
    }

    public boolean isPauseTimedOut(long timeoutMillis) {
        return pause && (System.currentTimeMillis() - pauseStartTime) > timeoutMillis;
    }

    private void handlePlayerDisconnect() {
        BotLogger.debug("🧠", isLogged(),
                bot.getId() + " 🚨 Игрок " + player.getName() + " отключился. Возврат к BrainTask.");
        //BotTaskManager.clear(bot);
        BotBrainTask brain = new BotBrainTask(bot);
        brain.setPause(false);
        BotTaskManager.push(bot, brain);
    }

    @Override
    public IBotTaskParameterized<T> setParams(T params) {
        this.params = params;
        startTime = System.currentTimeMillis();
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isDefered() {
        return deffered;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String obj) {
        this.objective = obj;
        BotLogger.debug(icon, isLogged(), bot.getId() + " 𖣠 Objective: " + obj);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icn) {
        this.icon = icn;
    }

    public Bot getBot() {
        return bot;
    }

    public String getUUID() {
        return uuid;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public boolean isReactive() {
        return isReactive;
    }

    public void setReactive(boolean reactive) {
        this.isReactive = reactive;
    }

    public void turnToTarget(BotTask<?> task, BotPosition target) {
        BotUtils.turnToTarget(task, bot, target);
    }

    public void animateHand(BotTask<?> task, Bot bot) {
        BotUtils.animateHand(task, bot);
    }
}
