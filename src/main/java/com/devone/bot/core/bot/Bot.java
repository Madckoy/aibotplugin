package com.devone.bot.core.bot;

import java.util.List;

import java.util.UUID;

import org.bukkit.Location;

import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;

import com.devone.bot.core.bot.bootstrap.BotBootstrap;
import com.devone.bot.core.bot.brain.BotBrain;
import com.devone.bot.core.bot.brain.logic.navigation.BotNavigation;
import com.devone.bot.core.bot.inventory.BotInventory;
import com.devone.bot.core.bot.speaker.BotSpeaker;
import com.devone.bot.core.bot.state.BotState;
import com.devone.bot.core.bot.task.active.move.BotMoveTask;
import com.devone.bot.core.bot.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

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
    private transient final BotBootstrap bootstrap; // Цикл жизни бота
    private final BotInventory inventory; // Инвентарь бота
    private transient final BotManager botManager; // Менеджер ботов

    private transient BotBrain brain; // Память/Рантайм статус бота
    private transient BotSpeaker speaker; // Создаем поле для общения бота
    private transient BotState state;

    private BotNavigation navigation;

    public void setBrain(BotBrain brain) {
        this.brain = brain;
    }


    public void setState(BotState state) {
        this.state = state;
    }
    public BotSpeaker getSpeaker() {
        return speaker;
    }


    public void setSpeaker(BotSpeaker speaker) {
        this.speaker = speaker;
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
        this.bootstrap = new BotBootstrap(this);
        this.brain = new BotBrain(this); // Инициализация рантайм статуса
        this.speaker = new BotSpeaker(this); // Инициализация BotCommunicator
        this.state = new BotState(this);
        this.navigation = new BotNavigation(this);

        BotLogger.debug("➕", true, "Has been CREATED AND SPAWNED: " + id);
    }

    public static BotTask<?> getActiveTask(Bot bot) {
        return bot.getBootstrap().getTaskStackManager().getActiveTask();
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
            BotLogger.debug("🤖", true, id + " ➖ Stopping ALL Tasks");
            
            BotUtils.clearTasks(this);

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

    public BotTask<?> getActiveTask() {
        BotTask<?> task = Bot.getActiveTask(this);
        return task;

    }
    // под вопросом, стоит ли перенести в BotUtils или в BotInventory
    public void checkAndSelfMove(Location target) {
        double pickupRadius = 2.0; // Радиус, в котором проверяем предметы
        List<Entity> nearbyItems = getNPCEntity().getNearbyEntities(pickupRadius, pickupRadius, pickupRadius);
    
        // Если есть дроп в радиусе 2 блоков — бот остается на месте
        if (!nearbyItems.isEmpty()) {
            BotLogger.debug("🤖",true, getId()+" 🔍 В радиусе " + pickupRadius + " блоков от есть предметы, остаюсь на месте.");
            return;
        }
    
        // Если предметов рядом нет, двигаем бота к последнему разрушенному блоку
        BotLocation loc = BotWorldHelper.worldLocationToBotLocation(target);
        BotLogger.debug("🤖", true, getId() + " 📦 Дроп подобран. Двигается к цели:" + loc);
        
        BotMoveTask mv_task = new BotMoveTask(this);
        BotMoveTaskParams mv_taskParams = new BotMoveTaskParams(loc);
        mv_task.setParams(mv_taskParams);

        BotUtils.pushTask(this, mv_task);
    }
}
