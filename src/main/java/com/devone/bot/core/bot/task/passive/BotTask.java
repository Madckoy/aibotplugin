package com.devone.bot.core.bot.task.passive;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.brain.BotBrainTask;
import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
import com.devone.bot.core.bot.task.reactive.BotReactivityManager;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;
import java.util.UUID;

public abstract class BotTask<T extends BotTaskParams> implements IBotTask, Listener, IBotTaskParameterized<T> {

    protected T params;

    protected boolean enabled = true;
    protected boolean stopped = false;
    protected boolean logging = true;

    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();

    private boolean pause = false;
    protected boolean done = false;

    protected final String uuid;
    protected String icon = "";
    protected String objective = "";

    protected boolean isListenerRegistered = false;
    private boolean isReactive = false;

    public BotTask(Bot bot) {
        this.bot = bot;
        this.uuid = UUID.randomUUID().toString();
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
        if (!enabled || isPause()) return;

        logTaskStatus();

        if (playerDisconnected()) {
            handlePlayerDisconnect();
            return;
        }

        if (handleReactiveLogic()) return;

        runTaskExecution();
    }

    private void logTaskStatus() {
        BotLogger.debug(icon, logging, bot.getId() +
            " ❓ Status: done=" + done + ", paused=" + pause +
            " 📍: " + bot.getNavigation().getLocation() +
            " | 🎯: " + bot.getNavigation().getTarget());
    }

    private boolean playerDisconnected() {
        return player != null && !player.isOnline();
    }

    private boolean handleReactiveLogic() {
        if (isReactive && !BotReactiveUtils.isAlreadyReacting(bot)) {
            BotLogger.debug("🧠", logging, bot.getId() + " ⚠️ Форсируем реактивный режим (task = " + getClass().getSimpleName() + ")");
            BotReactiveUtils.activateReaction(bot, true);
        }

        if (!BotReactiveUtils.isAlreadyReacting(bot)) {
            Optional<Runnable> reaction = BotReactivityManager.checkReactions(bot);
            if (reaction.isPresent()) {
                setPause(true);
                BotLogger.debug("🧠", logging, bot.getId() + " 🚨 Обнаружена реакция. Текущая задача приостановлена.");
                reaction.get().run();
                return true;
            }
        }

        return false;
    }

    private void runTaskExecution() {
        BotLogger.debug("🧠", logging, bot.getId() + " 🟡 Выполнение: " + icon + " " + getClass().getSimpleName());
        onStart();
        execute();
    }

    public abstract void execute();

    public void stop() {
        done = true;

        if (isReactive && BotReactiveUtils.isReactionOwnedBy(bot, this)) {
            BotLogger.debug("🧠", logging, bot.getId() + " 🧹 Завершена реактивная задача: " + getClass().getSimpleName());
            BotReactiveUtils.activateReaction(bot, false);
        }

        onEnd();
    }

    public void onStart() {
        // Хук для переопределения (например: логика инициализации)
    }

    public void onEnd() {
        // Хук для завершения задачи
    }

    public void setPause(boolean pause) {
        this.pause = pause;
        String status = pause ? "⏸️ Pause" : "▶️ Resume";
        BotLogger.debug(icon, logging, bot.getId() + " " + status + " (" + getClass().getSimpleName() + ")");
    }

    private void handlePlayerDisconnect() {
        BotLogger.debug("🧠", logging, bot.getId() + " 🚨 Игрок " + player.getName() + " отключился. Возврат к BrainTask.");
        BotUtils.clearTasks(bot);
        BotUtils.pushTask(bot, new BotBrainTask(bot));
        this.stop();
    }

    @Override
    public IBotTaskParameterized<T> setParams(T params) {
        this.params = params;
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLogging() {
        return logging;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String obj) {
        this.objective = obj;
        BotLogger.debug(icon, logging, bot.getId() + " 𖣠 Objective: " + obj);
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

    public void turnToTarget(BotTask<?> task, BotLocation target) {
        BotUtils.turnToTarget(task, bot, target);
    }

    public void animateHand(BotTask<?> task, Bot bot) {
        BotUtils.animateHand(task, bot);
    }
}
