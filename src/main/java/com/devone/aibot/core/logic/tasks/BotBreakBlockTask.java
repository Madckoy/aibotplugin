package com.devone.aibot.core.logic.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.ZoneManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.MaterialDetector;
import com.devone.aibot.AIBotPlugin;

public class BotBreakBlockTask implements BotTask {

    private final Bot bot;
    private Location targetLocation;
    private long startTime = System.currentTimeMillis();
    private String name = "BREAK";
    private MaterialDetector materialDetector;
    private int blocksMined = 0;
    private int maxBlocks;
    private int searchRadius;
    private int breakProgress = 0;
    private Set<Material> targetMaterials = null;
    private boolean isDone;

    private static final Map<Material, Integer> BREAK_TIME_PER_BLOCK = new HashMap<>();
    
    static {
        BREAK_TIME_PER_BLOCK.put(Material.DIRT, 5);  
        BREAK_TIME_PER_BLOCK.put(Material.STONE, 30); 
    }
    
    public BotBreakBlockTask(Bot bot) {
        this.bot = bot;
    }
    
    @Override
    public void configure(Object... params) {
        if (params.length >= 1) {
            if (params[0] instanceof Material) {
                targetMaterials = Set.of((Material) params[0]);
            } else if (params[0] instanceof Set) {
                targetMaterials = (Set<Material>) params[0];
                if (targetMaterials.isEmpty()) {
                    targetMaterials = null; 
                }
            }
        }
        
        if (params.length >= 2 && params[1] instanceof Integer) {
            this.searchRadius = (Integer) params[1];
        }

        if (params.length >= 3 && params[2] instanceof Integer) {
            this.maxBlocks = (Integer) params[2];
        }

        materialDetector = new MaterialDetector(searchRadius);
        BotLogger.debug("🔨 BreakBlockTask сконфигурирована: " + (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials));
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) { 
        BotLogger.debug("⏸ Пауза не поддерживается для " + name);
    }

    @Override
    public String getName() {
        return name;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    //-----------------------------------------------------------------
    @Override
    public void update() {
        if (isDone) return;
    
        if (targetLocation == null) {
            BotLogger.debug("[BotBreakBlockTask] "+bot.getId() + " is looking for a block to break. Target material - "+ targetMaterials);
            targetLocation = materialDetector.findClosestMaterialInSet(targetMaterials, bot.getNPCCurrentLocation());

            if (targetLocation == null) {
                BotLogger.debug(bot.getId() + " ❌ Нет доступных блоков для добычи!");
                isDone = true;
                return;
            }

            BotLogger.debug(bot.getId() + " 🛠️ Нашел " + targetLocation.getBlock().getType() + " на " + BotUtils.formatLocation(targetLocation));

        } else {
            BotLogger.debug("[BotBreakBlockTask] "+bot.getId() + " Wants to break a block of  "+ targetMaterials + " at " + BotUtils.formatLocation(targetLocation));
        }

        if (targetLocation.getBlock().getType() == Material.AIR) {
            BotLogger.debug("⚠️ Бот пытается ломать воздух! Меняем цель...");
            targetLocation = null; // Сбрасываем цель, чтобы бот искал заново
            return;
        }

        if (ZoneManager.getInstance().isInProtectedZone(targetLocation)) {
            BotLogger.debug("⛔ Бот " + bot.getId() + " в запретной зоне, НЕ разрушает блок " + BotUtils.formatLocation(targetLocation));
            isDone = true;
            return;
        }

        // Список всех блоков вокруг
        List<Location> blocksToMine = new ArrayList<>();
        Location[] adjacentLocations = {
            targetLocation.clone().add(1, 0, 0),
            targetLocation.clone().add(-1, 0, 0),
            targetLocation.clone().add(0, 0, 1),
            targetLocation.clone().add(0, 0, -1),
            targetLocation.clone().add(0, 1, 0)
        };

        // Проверяем соседние блоки
        for (Location loc : adjacentLocations) {
            if (targetMaterials == null || targetMaterials.contains(loc.getBlock().getType())) {
                blocksToMine.add(loc);
            }
        }

        // Если есть блоки вокруг – сначала копаем их
        if (!blocksToMine.isEmpty()) {
            targetLocation = blocksToMine.get(0);
            BotLogger.debug("🧱 Ломаем соседний блок " + BotUtils.formatLocation(targetLocation));
            return;
        }

        // Если вокруг ничего нет, проверяем блок ниже
        Location below = targetLocation.clone().add(0, -1, 0);
        if (targetMaterials == null || targetMaterials.contains(below.getBlock().getType())) {
            targetLocation = below;
            BotLogger.debug("🔽 Переходим на блок ниже " + BotUtils.formatLocation(below));
            return;
        }

        // Начинаем процесс разрушения
        Material blockType = targetLocation.getBlock().getType();
        int breakTime = BREAK_TIME_PER_BLOCK.getOrDefault(blockType, 10);

        if (breakProgress < breakTime) {
            breakProgress++;
            bot.getNPCEntity().getWorld().playEffect(targetLocation, org.bukkit.Effect.STEP_SOUND, blockType);
            BotLogger.debug(bot.getId() + " ⏳ Ломаем " + blockType + " [" + breakProgress + "/" + breakTime + "]");
            return;
        }

        // Разрушаем блок
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            if (targetLocation != null) {
                Material currentBlock = targetLocation.getBlock().getType();
                BotLogger.debug("🔍 Проверяем блок перед разрушением: " + currentBlock);

                if (currentBlock == blockType) {
                    targetLocation.getBlock().breakNaturally();
                    BotLogger.debug("✅ Блок разрушен на " + BotUtils.formatLocation(targetLocation));

                    blocksMined++;
                    breakProgress = 0;

                    // Повторная проверка соседних блоков
                    List<Location> newBlocksToMine = new ArrayList<>();
                    for (Location loc : adjacentLocations) {
                        if (targetMaterials == null || targetMaterials.contains(loc.getBlock().getType())) {
                            newBlocksToMine.add(loc);
                        }
                    }

                    if (!newBlocksToMine.isEmpty()) {
                        targetLocation = newBlocksToMine.get(0);
                        BotLogger.debug("🧱 Продолжаем ломать соседний блок " + BotUtils.formatLocation(targetLocation));
                        return;
                    }

                    // Если ничего не найдено, проверяем блок ниже
                    if (targetMaterials == null || targetMaterials.contains(below.getBlock().getType())) {
                        targetLocation = below;
                        BotLogger.debug("🔽 Переходим на блок ниже " + BotUtils.formatLocation(below));
                    } else {
                        targetLocation = null;
                        isDone = true;
                    }
                } else {
                    BotLogger.debug("⚠️ Блок уже изменен! " + BotUtils.formatLocation(targetLocation));
                }
            }
        });
    }
}
