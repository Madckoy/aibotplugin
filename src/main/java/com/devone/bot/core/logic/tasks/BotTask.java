package com.devone.bot.core.logic.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotTaskConfig;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotSceneData;
import com.devone.bot.utils.BotStringUtils;
import com.devone.bot.utils.BotUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BotTask implements IBotTask, IBotTaskConfigurable {

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
        BotLogger.info(this.isLogged(), "🚩 " + bot.getId() + "  Set Objective: " + objctv);
    }

    @Override
    public void update() {

        BotLogger.info(this.isLogged(), "🚦 " + bot.getId() + " " + name + " Status: " + isDone + " | " + isPaused +
                " 📍 xyz: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getCurrentLocation()) +
                " 🎯 xyz: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()) + " [ID: " + uuid + "]");

        if (isPaused) return;

        if (this.player != null && !isPlayerOnline()) {
            handlePlayerDisconnect();
        }

        if (isEnabled) {
            execute();
        }
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
        BotLogger.info(this.isLogged(), status + bot.getId() + " [ID: " + uuid + "]");
    }

    @Override
    public BotTask configure(Object... params) {
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

    public void handleStuck() {
        if (bot.getRuntimeStatus().getTargetLocation() != null) {
            if (bot.getNPCEntity() != null) {
                BotLogger.info(this.isLogged(), "✨ " + bot.getId() + " Застрял! Телепортируемся в " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));

                BotTeleportTask tp = new BotTeleportTask(bot, player);
                if (player != null) {
                    tp.configure(player.getLocation());
                } else {
                    tp.configure(bot.getRuntimeStatus().getTargetLocation());
                }

                bot.addTaskToQueue(tp);
            } else {
                BotLogger.info(this.isLogged(), "✨ " + bot.getId() + " Застрял! Нет Taget Location и нет NPC Entity!");
            }
        } else {
            if (bot.getNPCEntity() != null) {
                BotLogger.info(this.isLogged(), "✨ " + bot.getId() + " Застрял! Нет Taget Location! Телепортируемся в точку респавна!");

                BotTeleportTask tp = new BotTeleportTask(bot, player);

                if (player != null) {
                    tp.configure(player.getLocation());
                } else {
                    tp.configure(BotUtils.getFallbackLocation());
                }

                bot.addTaskToQueue(tp);
            } else {
                BotLogger.info(this.isLogged(), "✨ " + bot.getId() + " Застрял! Нет Taget Location и нет NPC Entity!");
            }
        }
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
}
