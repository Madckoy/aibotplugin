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
        Location botLoc = bot.getNPCCurrentLocation();
        int botY = botLoc.getBlockY();
        Random random = new Random();
    
        List<Location> sortedTargets = envMap.keySet().stream()
            .filter(loc -> loc.getBlockY() >= botY - 1 && loc.getBlockY() <= botY + 1) // Только ±1 уровень
            .filter(loc -> isBlockExposed(loc) && isValidTargetBlock(loc.getBlock().getType())) // Проверка видимости и валидности
            .sorted(Comparator.comparingInt(loc -> -loc.getBlockY())) // ❗ Теперь копаем сверху вниз
            .toList();
    
        for (Location candidate : sortedTargets) {
            Material blockType = candidate.getBlock().getType();
    
            // ❗ Проверяем, не зависает ли блок в воздухе
            if (gravityCheck(candidate)) continue; // ❌ Пропускаем висящие блоки
    
            // ❗ Проверка инструмента
            if (BotUtils.requiresTool(blockType) && !BotInventory.hasToolFor(bot, blockType)) {
                envMap.remove(candidate); // 🔥 Убираем, чтобы бот не пытался снова
                BotUtils.sendMessageToPlayer(null, bot.getId(), "I need a proper tool to break " + blockType + "!");
                return null; // Пропускаем этот блок
            }
    
            envMap.remove(candidate); // ✅ Удаляем из списка сканирования
            
            // 🌀 Добавляем случайный шум
            int offsetX = random.nextInt(3) - 1; // -1, 0 или +1
            int offsetZ = random.nextInt(3) - 1;
            int offsetY = (random.nextDouble() < 0.15) ? (random.nextBoolean() ? 1 : -1) : 0; // 🔄 15% шанс скорректировать высоту
    
            return candidate.clone().add(offsetX, offsetY, offsetZ);
        }
    
        return null; // Если ничего не нашли
    }
    
    
    private boolean isValidTargetBlock(Material blockType) {
        return blockType != Material.AIR && blockType != Material.WATER && blockType != Material.LAVA &&
               (targetMaterials == null || targetMaterials.contains(blockType));
    }
    
    private boolean gravityCheck(Location loc) {
        Location above = loc.clone().add(0, 1, 0);
        return above.getBlock().getType() == Material.AIR; // Если сверху воздух – не трогаем
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

    private boolean isBlockExposed(Location loc) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue; // Сам блок не трогаем
                    
                    Location neighbor = loc.clone().add(dx, dy, dz);
                    Material type = neighbor.getBlock().getType();
    
                    if (type == Material.AIR || type == Material.WATER || type == Material.LAVA) {
                        return true; // Блок видимый, если рядом воздух, вода или лава
                    }
                }
            }
        }
        return false; // Блок полностью окружён твёрдыми блоками
    }

}
