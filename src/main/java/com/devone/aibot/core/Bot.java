package com.devone.aibot.core;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;


import com.devone.aibot.core.logic.BotLifeCycle;
import com.devone.aibot.core.logic.tasks.BotTaskMove;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;

public class Bot {
    // private UUID uuid;
    private final String id; // Уникальное имя бота
    private NPC npc; // Связанный NPC
    private final BotLifeCycle lifeCycle; // Цикл жизни бота
    private Location targetLocation; // Куда бот должен идти

    private final BotInventory inventory; // Инвентарь бота
    private final BotManager botManager; // Менеджер ботов

    private boolean autoPickupEnabled = false;

    private Location lastKnownLocation = Bot.getFallbackLocation();

    public Bot(String id, NPC an_npc, BotManager botManager) {
 
        this.id = id;
        this.npc = an_npc;

        this.botManager = botManager;

        this.inventory = new BotInventory(this);

        this.lifeCycle = new BotLifeCycle(this);

        BotLogger.info("➕ "+ id + " Has been CREATED AND SPAWNED!");

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

    public void moveTo(Location location) {
        if (npc == null || !npc.isSpawned()) {
            BotLogger.error("❌ " + id + " Не может двигаться - NPC не заспавнен!");
            return;
        }

        if (location == null) {
            BotLogger.error("❌ " + id + " Получил null-локацию!");
            return;
        }

        BotLogger.info("🚶 " + id + " Направляется в " + BotStringUtils.formatLocation(location));

        this.targetLocation = location;
        npc.getNavigator().setTarget(location);
    }

    public void resetTargetLocation() {
        this.targetLocation = null;
    }

    public Location getTargetLocation() {
        return this.targetLocation;
    }

    public void say(String message) {
        if (npc != null && npc.getEntity() instanceof Player) {
            ((Player) npc.getEntity()).sendMessage("[Bot] " + message);
        }
    }

    public String getId() {
        return id;
    }

    public BotTask getCurrentTask() {
        return getLifeCycle().getTaskStackManager().getActiveTask();
    }

    public static Location getFallbackLocation() {
        World world = Bukkit.getWorlds().get(0);
        return world.getSpawnLocation();
    }

    public BotLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    public Location getNPCCurrentLocation() {
        if (npc != null) {
                //BotLogger.debug(getNPCDetails(npc));

                Location currLoc = npc.getStoredLocation();
                
                // BotLogger.info("ℹ️ " + id + " Current Location: " + BotStringUtils.formatLocation(currLoc));

                lastKnownLocation = currLoc;
            return currLoc;
        } else {
            BotLogger.error("❌ " + id + "Unable to get Current Location! NPC is not spawned. "+id);
            //
            // ✅ Пробуем взять сохраненное значение
            return lastKnownLocation;
        }
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

    @SuppressWarnings("unused")
    private String getNPCDetails(NPC npc) {
        String npc_props = npc.getName()+"|"+
                           npc.isSpawned()+"|"+
                           npc.getEntity()+"|"+
                           npc.getStoredLocation()+"|"+
                           npc.getNavigator().isNavigating()+"|"+
                           npc.getNavigator().isPaused();
  

        return npc_props;

    }

    public void setAutoPickupEnabled(boolean enabled) {
            this.autoPickupEnabled = enabled;
    }

    public void pickupNearbyItems(boolean shouldPickup) {
        getInventory().pickupAll(shouldPickup, autoPickupEnabled);
    }    

    public void checkAndSelfMove(Location lastBrokenBlock) {
        double pickupRadius = 2.0; // Радиус, в котором проверяем предметы
        List<Entity> nearbyItems = getNPCEntity().getNearbyEntities(pickupRadius, pickupRadius, pickupRadius);
    
        // Если есть дроп в радиусе 2 блоков — бот остается на месте
        if (!nearbyItems.isEmpty()) {
            BotLogger.info("✅ "+ getId() +"В радиусе " + pickupRadius + " блоков есть предметы, остаюсь на месте.");
            return;
        }
    
        // Если предметов рядом нет, двигаем бота к последнему разрушенному блоку
        BotLogger.info("✅ "+getId() +" Дроп подобран, двигаюсь к последнему разрушенному блоку " + lastBrokenBlock);
       
        BotTaskMove mv_task = new BotTaskMove(this);
        mv_task.configure(lastBrokenBlock);

        getLifeCycle().getTaskStackManager().pushTask(mv_task);
    }

    public BotTask getActiveTask() {
       return this.getLifeCycle().getTaskStackManager().getActiveTask();
    }    

}
