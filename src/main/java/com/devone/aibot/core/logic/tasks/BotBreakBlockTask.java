package com.devone.aibot.core.logic.tasks;

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.ZoneManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.BlockScanner3D;
import com.devone.aibot.AIBotPlugin;

public class BotBreakBlockTask implements BotTask {

    private final Bot bot;
    private Location targetLocation;
    private long startTime = System.currentTimeMillis();
    private String name = "BREAK";
    private int blocksMined = 0;
    private int maxBlocks;
    private int searchRadius;
    private int breakProgress = 0;
    private Set<Material> targetMaterials = null;
    private boolean isDone;
    private boolean shouldPickup = false; // ✅ Новый параметр
    private Map<Location, Material> scannedBlocks;
    
    private static final Map<Material, Integer> BREAK_TIME_PER_BLOCK = new HashMap<>();
    
    public BotBreakBlockTask(Bot bot) {
        this.bot = bot;
    }
    
    @Override
    public void configure(Object... params) {
        if (params.length >= 1 && params[0] instanceof Set) {
            targetMaterials = (Set<Material>) params[0];
            if (targetMaterials.isEmpty()) targetMaterials = null;
        }

        if (params.length >= 3 && params[1] instanceof Integer) {
            this.maxBlocks = (Integer) params[1];
        }

        if (params.length >= 2 && params[2] instanceof Integer) {
            this.searchRadius = (Integer) params[2];
        }

        if (params.length >= 4 && params[3] instanceof Boolean) { // ✅ Добавили параметр подбора
            this.shouldPickup = (Boolean) params[3];
        }

        BotLogger.debug("🔨 BreakBlockTask сконфигурирована: " + 
            (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials) +
            " | Подбор предметов: " + shouldPickup);
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

    @Override
    public void update() {
        if (isDone) return;
    
        if (targetLocation == null) {
            Map<Location, Material> scannedBlocks = BlockScanner3D.scanSurroundings(bot.getNPCCurrentLocation(), searchRadius);

            for (Map.Entry<Location, Material> entry : scannedBlocks.entrySet()) {
                Location loc = entry.getKey();
                Material material = entry.getValue();
                
                if (targetMaterials.contains(material)) {
                    targetLocation = loc;
                    BotLogger.debug(bot.getId() + " 🛠️ Нашел " + material + " на " + BotUtils.formatLocation(targetLocation));
                    break;
                }
            }
            
            bot.getNPCEntity().teleport(bot.getNPCCurrentLocation().setDirection(targetLocation.toVector().subtract(bot.getNPCCurrentLocation().toVector())));

            if (targetLocation == null) {
                BotLogger.debug(bot.getId() + " ❌ Нет доступных блоков для добычи!");
                isDone = true;
                return;
            }
        }

        if (targetLocation.getBlock().getType() == Material.AIR) {
            BotLogger.debug("⚠️ Бот пытается ломать воздух! Меняем цель...");
            targetLocation = null;
            return;
        }

        if (ZoneManager.getInstance().isInProtectedZone(targetLocation)) {
            BotLogger.debug("⛔ Бот " + bot.getId() + " в запретной зоне, НЕ разрушает блок " + BotUtils.formatLocation(targetLocation));
            isDone = true;
            return;
        }

        int breakTime = BREAK_TIME_PER_BLOCK.getOrDefault(targetLocation.getBlock().getType(), 5); // ⏳ Ускорил процесс в 2 раза
        if (breakProgress < breakTime) {
            breakProgress++;
            bot.getNPCEntity().getWorld().playEffect(targetLocation, org.bukkit.Effect.STEP_SOUND, targetLocation.getBlock().getType());
            BotLogger.debug(bot.getId() + " ⏳ Ломаем " + targetLocation.getBlock().getType() + " [" + breakProgress + "/" + breakTime + "]");
            return;
        }

        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            if (targetLocation != null && targetLocation.getBlock().getType() != Material.AIR) {
                targetLocation.getBlock().breakNaturally();
                BotLogger.debug("✅ Блок разрушен на " + BotUtils.formatLocation(targetLocation));
                blocksMined++;
                breakProgress = 0;
                
                if (shouldPickup) { // ✅ Если нужно подбирать - вызываем метод
                    bot.pickupNearbyItems();
                }

                targetLocation = null;
            }
        });
    }
}
