package com.devone.aibot.core;

import java.util.List;

import java.util.UUID;

import org.bukkit.Location;

import org.bukkit.entity.Entity;

import com.devone.aibot.core.comms.BotCommunicator;
import com.devone.aibot.core.logic.BotLifeCycle;
import com.devone.aibot.core.logic.tasks.BotTaskMove;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.utils.BotLogger;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;

public class Bot {
    private final String id; // Уникальное имя бота
    private NPC npc; // Связанный NPC
    private final BotLifeCycle lifeCycle; // Цикл жизни бота
    private final BotInventory inventory; // Инвентарь бота
    private final BotManager botManager; // Менеджер ботов
    private boolean autoPickupEnabled = false;
    private BotRuntimeStatus rStatus; // Рантайм статус бота
    private BotCommunicator communicator; // Создаем поле для общения бота

    public Bot(String id, NPC an_npc, BotManager botManager) {
        this.id = id;
        this.npc = an_npc;
        this.botManager = botManager;
        this.inventory = new BotInventory(this);
        this.lifeCycle = new BotLifeCycle(this);
        this.rStatus = new BotRuntimeStatus(this); // Инициализация рантайм статуса
        this.communicator = new BotCommunicator(this); // Инициализация BotCommunicator
        BotLogger.info("➕ " + id + " Has been CREATED AND SPAWNED!");
    }

    // Getter для общения
    public BotCommunicator getCommunicator() {
        return communicator;
    }

    public NPC getNPC() {
        return npc;
    }

    public UUID getUuid() {
        if (npc != null) {
            return npc.getUniqueId();
        } else {
            return new UUID(0, 0);
        }
    }

    public BotRuntimeStatus getRuntimeStatus() {
        return rStatus;
    }

    public void despawnNPC() {
        if (npc != null) {
            //stop all tasks!
            BotLogger.info("➖ "+ id + " Stopping ALL Tasks!");
            getLifeCycle().getTaskStackManager().clearTasks();

            BotLogger.info("➖ "+ id + " Despawning and Destroying NPC");
            npc.despawn();
            npc.destroy();
            npc = null;
        }
        BotLogger.info("➖ "+ id + " Has been DESPAWNED and DESTROYED!");
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

    public BotTask getCurrentTask() {
        return getLifeCycle().getTaskStackManager().getActiveTask();
    }

    public BotLifeCycle getLifeCycle() {
        return lifeCycle;
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

    

    public void addTaskToQueue(BotTask task) {
      getLifeCycle().getTaskStackManager().pushTask(task);
    }

    public void setAutoPickupEnabled(boolean enabled) {
            this.autoPickupEnabled = enabled;
    }

    public void pickupNearbyItems(boolean shouldPickup) {
        getInventory().pickupAll(shouldPickup, autoPickupEnabled);
    }    
    // под вопросом, стоит ли перенести в BotUtils или в BotInventory
    public void checkAndSelfMove(Location lastBrokenBlock) {
        double pickupRadius = 2.0; // Радиус, в котором проверяем предметы
        List<Entity> nearbyItems = getNPCEntity().getNearbyEntities(pickupRadius, pickupRadius, pickupRadius);
    
        // Если есть дроп в радиусе 2 блоков — бот остается на месте
        if (!nearbyItems.isEmpty()) {
            BotLogger.debug("✅ "+ getId() +"В радиусе " + pickupRadius + " блоков есть предметы, остаюсь на месте.");
            return;
        }
    
        // Если предметов рядом нет, двигаем бота к последнему разрушенному блоку
        BotLogger.debug("✅ "+getId() +" Дроп подобран, двигаюсь к последнему разрушенному блоку " + lastBrokenBlock);
        
        BotTaskMove mv_task = new BotTaskMove(this);
        mv_task.configure(lastBrokenBlock);
        addTaskToQueue(mv_task);
    }

    public BotTask getActiveTask() {
       return this.getLifeCycle().getTaskStackManager().getActiveTask();
    }    
}
