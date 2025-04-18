package com.devone.bot.core.bot;

import java.util.List;

import java.util.UUID;

import org.bukkit.Location;

import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.bot.brain.BotBrain;
import com.devone.bot.core.bot.inventory.BotInventory;
import com.devone.bot.core.bot.navigation.BotNavigation;
import com.devone.bot.core.bot.speaker.BotSpeaker;
import com.devone.bot.core.bot.state.BotState;
import com.devone.bot.core.logic.lifecycle.BotLifeCycle;
import com.devone.bot.core.logic.task.move.BotMoveTask;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.logger.BotLogger;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;

public class Bot {
    private boolean isEnabled = true;
    private boolean isLogging = true;
    private boolean allowPickupItems = true;

    public boolean isLogging() {
        return isLogging;
    }

    private final String id; // Уникальное имя бота
    private NPC npc; // Связанный NPC
    private final BotLifeCycle lifeCycle; // Цикл жизни бота
    private final BotInventory inventory; // Инвентарь бота
    private final BotManager botManager; // Менеджер ботов
    private BotBrain brain; // Память/Рантайм статус бота
    private BotSpeaker speaker; // Создаем поле для общения бота
    public BotSpeaker getSpeaker() {
        return speaker;
    }


    public void setSpeaker(BotSpeaker speaker) {
        this.speaker = speaker;
    }

    private BotState state;
    private BotNavigation navigation;

    public void setBrain(BotBrain brain) {
        this.brain = brain;
    }


    public void setState(BotState state) {
        this.state = state;
    }


    public BotNavigation getNavigation() {
        return navigation;
    }


    public void setNavigation(BotNavigation nav) {
        this.navigation = nav;
    }


    public BotState getState() {
        return state;
    }


    public Bot(String id, NPC an_npc, BotManager botManager) {
        this.id = id;
        this.npc = an_npc;
        this.botManager = botManager;
        this.inventory = new BotInventory(this);
        this.lifeCycle = new BotLifeCycle(this);
        this.brain = new BotBrain(this); // Инициализация рантайм статуса
        this.speaker = new BotSpeaker(this); // Инициализация BotCommunicator
        this.state = new BotState(this);
        this.navigation = new BotNavigation(this);

        BotLogger.debug("➕", true, "Has been CREATED AND SPAWNED: " + id);
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

    public void setLogging(boolean isLogging) {
        this.isLogging = isLogging;
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
            //stop all tasks!
            BotLogger.debug("➖", true, "Stopping ALL Tasks for: " + id);
            getLifeCycle().getTaskStackManager().clearTasks();

            BotLogger.debug("➖", true, " Despawning and Destroying NPC for: "+id );
            npc.despawn();
            npc.destroy();
            npc = null;
        }
        BotLogger.debug("➖", true, "Has been DESPAWNED and DESTROYED: "+ id);
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

    public void pickupNearbyItems() {
        getInventory().pickupAll(this.allowPickupItems);
    }    
    // под вопросом, стоит ли перенести в BotUtils или в BotInventory
    public void checkAndSelfMove(Location lastBrokenBlock) {
        double pickupRadius = 2.0; // Радиус, в котором проверяем предметы
        List<Entity> nearbyItems = getNPCEntity().getNearbyEntities(pickupRadius, pickupRadius, pickupRadius);
    
        // Если есть дроп в радиусе 2 блоков — бот остается на месте
        if (!nearbyItems.isEmpty()) {
            BotLogger.debug("✅",true, "В радиусе " + pickupRadius + " блоков от "+ getId() +" есть предметы, остаюсь на месте.");
            return;
        }
    
        // Если предметов рядом нет, двигаем бота к последнему разрушенному блоку
        BotLogger.debug("✅ ", true, "Дроп подобран "+ getId() +" и двигается к последнему разрушенному блоку " + lastBrokenBlock);
        
        BotMoveTask mv_task = new BotMoveTask(this);
        BotLocation loc = new BotLocation(lastBrokenBlock.getBlockX(), lastBrokenBlock.getBlockY(), lastBrokenBlock.getBlockZ());
        BotMoveTaskParams mv_taskParams = new BotMoveTaskParams(loc);
        mv_task.setParams(mv_taskParams);

        getLifeCycle().getTaskStackManager().pushTask(mv_task);
    }
}
