package com.devone.aibot.core.logic.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BotTask implements IBotTask {

    protected Bot bot;
    protected Player player = null;
    protected long startTime = System.currentTimeMillis();
    protected String name = "☑️";
    protected boolean isPaused = false;
    protected boolean isDone = false;
    protected Location targetLocation;
    protected boolean isEnabled = true;
    protected final String uuid;
    protected Map<Location, Material> geoMap;
    protected List<LivingEntity> bioEntities;
    protected String objective;

    protected BotTaskConfig config;

    public BotTask(Bot bot) {
        this.bot = bot;
        this.uuid = UUID.randomUUID().toString();
        this.config = new BotTaskConfig(null);
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

    public void setEnvMap(Map<Location, Material> env_map) {
        geoMap = env_map;
    }

    public Map<Location, Material> getEnvMap() {
        return geoMap;
    }

    public Map<Location, Material> getGeoMap() {
        return geoMap;
    }

    public void setGeoMap(Map<Location, Material> geoMap) {
        this.geoMap = geoMap;
    }

    public List<LivingEntity> getBioEntities() {
        return bioEntities;
    }

    public void setBioEntities(List<LivingEntity> bioEntities) {
        this.bioEntities = bioEntities;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objctv) {
        objective = objctv;
        BotLogger.trace("🚩 " + bot.getId() + "  Set Objective: " + objctv);
    }

    @Override
    public void update() {
        BotLogger.trace("🚦 " + bot.getId() + " " + name + " Status: " + isDone + " | " + isPaused +
                " 📍 xyz: " + BotStringUtils.formatLocation(bot.getNPCCurrentLocation()) +
                " 🎯 xyz: " + BotStringUtils.formatLocation(targetLocation) + " [ID: " + uuid + "]");

        if (isPaused) return;

        if (this.player != null && !isPlayerOnline()) {
            handlePlayerDisconnect();
        }

        if (isEnabled) {
            executeTask();
        }
    }

    public abstract void executeTask();

    public String getUUID() {
        return uuid;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        String status = isPaused ? "⏸️ Pausing..." : "▶️ Resuming...";
        BotLogger.debug(status + bot.getId() + " [ID: " + uuid + "]");
    }

    @Override
    public BotTask configure(Object... params) {
        startTime = System.currentTimeMillis();
        return this;
    }

    public BotTaskConfig getConfig() {
        return config;
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
        if (targetLocation != null) {
            if (bot.getNPCEntity() != null) {
                BotLogger.trace("✨ " + bot.getId() + " Застрял! Телепортируемся в " + BotStringUtils.formatLocation(targetLocation));

                BotTaskTeleport tp = new BotTaskTeleport(bot, player);
                if (player != null) {
                    tp.configure(player.getLocation());
                } else {
                    tp.configure(targetLocation);
                }

                bot.addTaskToQueue(tp);
            } else {
                BotLogger.error("✨ " + bot.getId() + " Застрял! Нет Taget Location и нет NPC Entity!");
            }
        } else {
            if (bot.getNPCEntity() != null) {
                BotLogger.trace("✨ " + bot.getId() + " Застрял! Нет Taget Location! Телепортируемся в точку респавна!");

                BotTaskTeleport tp = new BotTaskTeleport(bot, player);

                if (player != null) {
                    tp.configure(player.getLocation());
                } else {
                    tp.configure(Bot.getFallbackLocation());
                }

                bot.addTaskToQueue(tp);
            } else {
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
