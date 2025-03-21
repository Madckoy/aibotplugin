package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.BotInventory;
import java.util.*;

import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.BotEnv3DScan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotZoneManager;
import com.devone.aibot.core.logic.tasks.configs.BotTaskBreakBlockConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.AIBotPlugin;

public class BotTaskBreakBlock extends BotTask {

    private int maxBlocks;
    private int searchRadius;
    private boolean shouldPickup = true;
    private boolean destroyAllIfNoTarget = false;
    private Set<Material> targetMaterials = null;
    //private BotTaskBreakBlockConfig config;
    private Location targetLocation;

    public BotTaskBreakBlock(Bot bot) {
        super(bot, "⛏️");
        config = new BotTaskBreakBlockConfig();
    }

    @Override
    public BotTask configure(Object... params) {
        super.configure(params);
        if (params.length >= 1 && params[0] instanceof Set) {
            targetMaterials = (Set<Material>) params[0];
            if (targetMaterials.isEmpty()) targetMaterials = null;
        }
        if (params.length >= 2 && params[1] instanceof Integer) {
            this.maxBlocks = (Integer) params[1];
        }
        if (params.length >= 3 && params[2] instanceof Integer) {
            this.searchRadius = (Integer) params[2];
        }
        if (params.length >= 4 && params[3] instanceof Boolean) {
            this.shouldPickup = (Boolean) params[3];
        }
        if (params.length >= 5 && params[4] instanceof Boolean) {
            this.destroyAllIfNoTarget = (Boolean) params[4];
        }
        bot.setAutoPickupEnabled(shouldPickup);
        BotLogger.debug("⚙️ BotTaskBreakBlock настроена: " + (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials));
        return this;
    }

    public void setTargetMaterials(Set<Material> materials) {
        this.targetMaterials = materials;
        BotLogger.trace("🎯 Установлены целевые блоки: " + materials);
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.trace("📜 Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void executeTask() {
        BotLogger.trace("🚀 Запуск задачи разрушения блоков для бота " + bot.getId() + " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");


        if (isInventoryFull() || isEnoughBlocksCollected()) {
            BotLogger.trace("⛔ Задача завершена: инвентарь полон или ресурсов достаточно");
            isDone = true;
            return;
        }

        bot.pickupNearbyItems(shouldPickup);

        if (getEnvMap() == null) {
            BotLogger.trace("🔍 Запускаем 3D-сканирование окружающей среды.");
            bot.addTaskToQueue(new BotTaskSonar3D(bot, this, searchRadius, searchRadius));
            isDone = false;
            return;
        }

        targetLocation = findNextTargetBlock();

        if (targetLocation != null) {
 
            if (isInProtectedZone(targetLocation)) {
                BotLogger.debug("⛔ " + bot.getId() + " в запретной зоне, НЕ будет разрушать блок: " + BotStringUtils.formatLocation(targetLocation));
                isDone = true;
                return;
            }

            BotLogger.trace("🛠️ Целевой блок найден: " + BotStringUtils.formatLocation(targetLocation));

            // Set<Material> targetMaterials = getTargetMaterials();

            setObjective("Разрушение блока: " + BotUtils.getBlockName(targetLocation.getBlock()));
   

            BotLogger.trace("🚧 " + bot.getId() + " Разрушение блока: " + targetLocation.getBlock().toString());
        
            BotTaskUseHand hand_task = new BotTaskUseHand(bot);
            hand_task.configure(targetLocation);
            bot.addTaskToQueue(hand_task);

        } else {

            handleNoTargetFound();
        }
    }

    private Location findNextTargetBlock() {
        Map<Location, Material> envMap = getEnvMap();
    
        if (envMap == null || envMap.isEmpty()) {
            BotLogger.trace("🔄 EnvMap пустая, перезапускаем сканер...");
            bot.addTaskToQueue(new BotTaskSonar3D(bot, this, searchRadius, searchRadius));
            return null;
        }
    
        Location botLoc = bot.getNPCCurrentLocation();
        int botY = botLoc.getBlockY();
    
        Location target = null;
        Iterator<Map.Entry<Location, Material>> iterator = envMap.entrySet().iterator();
    
        // 🔥 1. Сначала ищем блоки на своём уровне (приоритетный уровень)
        while (iterator.hasNext()) {
            Map.Entry<Location, Material> entry = iterator.next();
            Location candidate = entry.getKey();
            Material blockType = entry.getValue();
    
            if (candidate.getBlockY() == botY) { // 🔹 Проверяем только блоки на текущем уровне
                if (isValidTargetBlock(blockType)) {
                    target = candidate;
                    iterator.remove();
                    return target;
                }
            }
        }
    
        // 🔥 2. Если не нашли, ищем выше и ниже
        iterator = envMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, Material> entry = iterator.next();
            Location candidate = entry.getKey();
            Material blockType = entry.getValue();
    
            if (isValidTargetBlock(blockType)) {
                target = candidate;
                iterator.remove();
                return target;
            }
        }
    
        BotLogger.trace("🔎 Поиск целевого блока: " + (target != null ? "найден" : "не найден"));
        return target;
    }
    
    private boolean isValidTargetBlock(Material blockType) {
        return blockType != Material.AIR && blockType != Material.WATER && blockType != Material.LAVA &&
               (targetMaterials == null || targetMaterials.contains(blockType));
    }
    
    

    private void handleNoTargetFound() {
        if (destroyAllIfNoTarget) {
            BotLogger.trace("🔄 " + bot.getId() + " Целевых блоков нет! Запускаем полное разрушение.");
            bot.addTaskToQueue(new BotTaskBreakBlockAny(bot));
            isDone = false;
        } else {
            setObjective("");  
            BotLogger.trace("❌ " + bot.getId() + " Нет подходящих блоков. Завершаем.");
            isDone = true;
        }
    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
        BotLogger.trace("📦 Проверка инвентаря: " + (full ? "полон" : "есть место"));
        return full;
    }

    private boolean isEnoughBlocksCollected() {
        boolean enough = BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
        BotLogger.trace("📊 Проверка количества блоков: " + (enough ? "достаточно" : "нужно больше"));
        return enough;
    }

    private boolean isInProtectedZone(Location location) {
        boolean protectedZone = BotZoneManager.getInstance().isInProtectedZone(location);
        if (protectedZone) {
            BotLogger.trace("🛑 Блок в запретной зоне, разрушение запрещено.");
        }
        return protectedZone;
    }

}
