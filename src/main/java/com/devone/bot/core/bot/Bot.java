package com.devone.bot.core.bot;

import java.util.List;

import java.util.UUID;

import org.bukkit.Location;

import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;

import com.devone.bot.core.chat.BotChat;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.logic.lifecycle.BotLifeCycle;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.move.BotMoveTask;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.core.status.BotStatusRuntime;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;

public class Bot {
    private final String id; // Уникальное имя бота
    private NPC npc; // Связанный NPC
    private final BotLifeCycle lifeCycle; // Цикл жизни бота
    private final BotInventory inventory; // Инвентарь бота
    private final BotManager botManager; // Менеджер ботов
    private BotStatusRuntime rStatus; // Рантайм статус бота
    private BotChat chat; // Создаем поле для общения бота


    public Bot(String id, NPC an_npc, BotManager botManager) {
        this.id = id;
        this.npc = an_npc;
        this.botManager = botManager;
        this.inventory = new BotInventory(this);
        this.lifeCycle = new BotLifeCycle(this);
        this.rStatus = new BotStatusRuntime(this); // Инициализация рантайм статуса
        this.chat = new BotChat(this); // Инициализация BotCommunicator
        BotLogger.info("➕", true, "Has been CREATED AND SPAWNED: " + id);
    }

    // Getter для общения
    public BotChat getCommunicator() {
        return chat;
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

    public BotStatusRuntime getRuntimeStatus() {
        return rStatus;
    }

    public void despawnNPC() {
        if (npc != null) {
            //stop all tasks!
            BotLogger.info("➖", true, "Stopping ALL Tasks for: " + id);
            getLifeCycle().getTaskStackManager().clearTasks();

            BotLogger.info("➖", true, " Despawning and Destroying NPC for: "+id );
            npc.despawn();
            npc.destroy();
            npc = null;
        }
        BotLogger.info("➖", true, "Has been DESPAWNED and DESTROYED: "+ id);
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

    public void pickupNearbyItems(boolean shouldPickup) {
        getInventory().pickupAll(shouldPickup);
    }    
    // под вопросом, стоит ли перенести в BotUtils или в BotInventory
    public void checkAndSelfMove(Location lastBrokenBlock) {
        double pickupRadius = 2.0; // Радиус, в котором проверяем предметы
        List<Entity> nearbyItems = getNPCEntity().getNearbyEntities(pickupRadius, pickupRadius, pickupRadius);
    
        // Если есть дроп в радиусе 2 блоков — бот остается на месте
        if (!nearbyItems.isEmpty()) {
            BotLogger.info("✅",true, "В радиусе " + pickupRadius + " блоков от "+ getId() +" есть предметы, остаюсь на месте.");
            return;
        }
    
        // Если предметов рядом нет, двигаем бота к последнему разрушенному блоку
        BotLogger.info("✅ ", true, "Дроп подобран "+ getId() +" и двигается к последнему разрушенному блоку " + lastBrokenBlock);
        
        BotMoveTask mv_task = new BotMoveTask(this);
        BotMoveTaskParams mv_taskParams = new BotMoveTaskParams(new BotCoordinate3D(lastBrokenBlock.getBlockX(), lastBrokenBlock.getBlockY(), lastBrokenBlock.getBlockZ()));
        mv_task.configure(mv_taskParams);
        addTaskToQueue(mv_task);
    }
}
