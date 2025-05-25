package com.devone.bot.core;

import java.util.UUID;

import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;

import com.devone.bot.core.bootstrap.BotBootstrap;
import com.devone.bot.core.brain.BotBrain;
import com.devone.bot.core.brain.navigator.BotNavigator;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.speaker.BotSpeaker;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.logger.BotLogger;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;

public class Bot {
    private boolean isEnabled = true;
    private boolean isLogged = true;

    private boolean allowPickupItems = true;

    public boolean isLogged() {
        return isLogged;
    }

    private final String id; // Уникальное имя бота
    private NPC npc; // Связанный NPC
    private transient final BotBootstrap bootstrap; // Цикл жизни бота
    private final BotInventory inventory; // Инвентарь бота
    private transient final BotManager botManager; // Менеджер ботов

    private transient BotBrain brain; // Память/Рантайм статус бота
    private transient BotSpeaker speaker; // Создаем поле для общения бота

    private BotNavigator navigator;


    public void setBrain(BotBrain brain) {
        this.brain = brain;
    }


    public BotSpeaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(BotSpeaker speaker) {
        this.speaker = speaker;
    }

    public BotNavigator getNavigator() {
        return navigator;
    }

    public void setNavigation(BotNavigator nav) {
        this.navigator = nav;
    }

    public Bot(String id, NPC an_npc, BotManager botManager) {
        this.id = id;
        this.npc = an_npc;
        this.botManager = botManager;
        this.inventory = new BotInventory(this);
        this.bootstrap = new BotBootstrap(this);
        this.brain = new BotBrain(this); // Инициализация рантайм статуса
        this.speaker = new BotSpeaker(this); // Инициализация BotCommunicator
        this.navigator = new BotNavigator(this);

        BotLogger.debug("🤖", true, id + " ➕ Has been created and spawned");
    }

    public static BotTask<?> getActiveTask(Bot bot) {
        return bot.getBootstrap().getTaskManager().getActiveTask();
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isAllowPickupItems() {
        return allowPickupItems;
    }

    public void setAllowPickupItems(boolean pickupItems) {
        this.allowPickupItems = pickupItems;
    }

    public void setisLogged(boolean log) {
        this.isLogged = log;
    }

    public NPC getNpc() {
        return npc;
    }

    public NPC getNPC() {
        return npc;
    }

    public Player getPlayer() {
        if (this.npc != null && this.npc.isSpawned()) {
            Entity entity = this.npc.getEntity();
            if (entity instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    public UUID getUuid() {
        if (npc != null) {
            return npc.getUniqueId();
        } else {
            return new UUID(0, 0);
        }
    }

    public BotBrain getBrain() {
        return brain;
    }

    public void despawnNPC() {
        if (npc != null) {
            // stop all tasks!
            BotLogger.debug("🤖", true, id + " ➖ Stopping All Tasks");

            BotTaskManager.clear(this);

            BotLogger.debug("🤖", true, id + " ➖ Despawning and Destroying NPC");
            npc.despawn();
            npc.destroy();
            npc = null;
        }
        BotLogger.debug("🤖", true, id + " ➖ Has been Despawned and Destroyed");
    }

    public BotInventory getInventory() {
        return inventory;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public String getId() {
        return id;
    }

    public BotBootstrap getBootstrap() {
        return bootstrap;
    }

    public Entity getNPCEntity() {
        return npc.getEntity();
    }

    public Navigator getNPCNavigator() {
        return npc.getNavigator();
    }

    public boolean isNPCSpawned() {
        return npc.isSpawned();
    }

    public void pickupNearbyItems() {
        getInventory().pickupAll(this.allowPickupItems);
    }

    public BotTask<?> getActiveTask() throws Exception
    {
        BotTask<?> task = getTaskManager().getActiveTask();
        if (task==null) {
            throw new Exception("Task is null");
        } else {
            return task;
        }      
    }

    public BotTaskManager getTaskManager() {
        return this.bootstrap.getTaskManager();
    }
}
