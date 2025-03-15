package com.devone.aibot.core;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.devone.aibot.core.logic.BotLifeCycle;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.utils.BotLogger;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;

public class Bot {
    // private UUID uuid;
    private final String id; // Уникальное имя бота
    private NPC npc; // Связанный NPC
    private BotStatus status = BotStatus.IDLING; // Текущее состояние
    private BotGoal currentGoal = BotGoal.IDLE; // Цель
    private final BotLifeCycle lifeCycle; // Цикл жизни бота
    private Location targetLocation; // Куда бот должен идти

    private final BotInventory inventory; // Инвентарь бота
    private final BotManager botManager; // Менеджер ботов

    private Location lastKnownLocation = Bot.getFallbackLocation();

    public Bot(String id, NPC an_npc, BotManager botManager) {
 
        this.id = id;
        this.npc = an_npc;

        this.botManager = botManager;

        this.inventory = new BotInventory(this);

        this.lifeCycle = new BotLifeCycle(this);

        BotLogger.debug("Бот " + id + " успешно создан!");

    }

    public UUID getUuid() {
        if (npc != null) {
            return npc.getUniqueId();
        } else {
            return new UUID(0, 0);
        }
    }

    public void despawn() {
        if (npc != null) {
            npc.despawn();
            npc.destroy();
            npc = null;
        }
        BotLogger.debug("Бот " + id + " деспавнен.");
    }  

    public BotInventory getInventory() {
        return inventory;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public void moveTo(Location location) {
        if (npc == null || !npc.isSpawned()) {
            BotLogger.debug("❌ Бот " + id + " не может двигаться - NPC не заспавнен!");
            return;
        }

        if (location == null) {
            BotLogger.debug("❌ Ошибка: `moveTo()` получил null-локацию!");
            return;
        }

        BotLogger.debug("🚶 Бот " + id + " направляется в " + formatLocation(location));

        this.targetLocation = location;
        npc.getNavigator().setTarget(location);
    }

    public void resetTargetLocation() {
        this.targetLocation = null;
    }

    public Location getTargetLocation() {
        return this.targetLocation;
    }

    public void setGoal(BotGoal goal) {
        this.currentGoal = goal;

        //botManager.saveBots(); // ✅ Сохраняем цель

        BotLogger.debug("Бот " + id + " получил новую цель: " + (goal != null ? goal.name() : "null"));
    }

    public BotGoal getGoal() {
        return this.currentGoal;
    }

    // ✅ Восстановленный метод `getCurrentGoal()`
    public BotGoal getCurrentGoal() {
        return currentGoal;
    }

    public void say(String message) {
        if (npc != null && npc.getEntity() instanceof Player) {
            ((Player) npc.getEntity()).sendMessage("[Bot] " + message);
        }
    }

    public String getId() {
        return id;
    }

    public BotStatus getStatus() {
        return status;
    }

    public void setStatus(BotStatus status) {
        this.status = status;
    }

    public BotTask getCurrentTask() {
        return getLifeCycle().getTaskStackManager().getCurrentTask();
    }

    public static Location getFallbackLocation() {
        World world = Bukkit.getWorlds().get(0);
        return world.getSpawnLocation();
    }

    public BotLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    private String formatLocation(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";

    }

    public Location getNPCCurrentLocation() {
        if (npc != null) {
                //BotLogger.debug(getNPCDetails(npc));

                Location currLoc = npc.getStoredLocation();
                
                BotLogger.debug("Bot/NPC " + id + " Current Location " + currLoc);
                lastKnownLocation = currLoc;
            return currLoc;
        } else {
            BotLogger.debug("Unable to get Current Location ! NPC is not spawned. "+id);
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
}
