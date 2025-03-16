package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.BotInventory;
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
    private Map<Location, Material> scannedBlocks;
    private boolean shouldPickup = true;
    
    private Queue<Location> pendingBlocks = new LinkedList<>(); // Очередь блоков рядом

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

        if (params.length >= 2 && params[1] instanceof Integer) {
            this.maxBlocks = (Integer) params[1];
        }

        if (params.length >= 3 && params[2] instanceof Integer) {
            this.searchRadius = (Integer) params[2];
        }
        
        if (params.length >= 4 && params[3] instanceof Integer) {
            this.shouldPickup = (boolean) params[3];
        }


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

    @Override
    public void update() {
        BotLogger.debug(bot.getId() + " Running task: " + name);

        if (isDone) return;

        if(!BotInventory.hasFreeInventorySpace(bot, targetMaterials)) {
            BotLogger.debug(bot.getId() + " 🔄 Not enoguh free space inInventory!");
            isDone = true;
            return;
        }

        //
        if (BotInventory.hasCollectedEnoughBlocks(bot, targetMaterials, maxBlocks)){
            BotLogger.debug(bot.getId() + " 🔄 Colleacted enough materials! Stopping the task");
            isDone = true;
            return;
        }

        // pickup items
        bot.pickupNearbyItems(shouldPickup);
        
        bot.setAutoPickupEnabled(shouldPickup);

        if (targetLocation == null) {
            if (!pendingBlocks.isEmpty()) {
                targetLocation = pendingBlocks.poll(); // Берем следующий блок из очереди
                BotLogger.debug(bot.getId() + " 🔄 Переход к следующему блоку " + BotUtils.formatLocation(targetLocation));
            } else {
                // Получаем карту блоков в радиусе поиска
                Map<Location, Material> scannedBlocks = BlockScanner3D.scanSurroundings(bot.getNPCCurrentLocation(), searchRadius);

                targetLocation = findNearestTargetBlock(scannedBlocks);

                if (targetLocation == null) {
                    BotLogger.debug(bot.getId() + " ❌ Нет доступных блоков для добычи! Перемещаемся к новой цели.");
                                               
                        Location newLocation = findNearestTargetBlock(scannedBlocks);

                        if(newLocation!=null) {

                            BotMoveTask moveTask = new BotMoveTask(bot);
                            moveTask.configure(newLocation);
                      
                            bot.getLifeCycle().getTaskStackManager().pushTask(moveTask); // Перемещаем бота чере новый таск

                           return;

                        } else {
                            //  Stop Task and exit
                            isDone = true;
                            return;
                        }
                }

                BotLogger.debug(bot.getId() + " 🛠️ Нашел " + targetLocation.getBlock().getType() + " на " + BotUtils.formatLocation(targetLocation));

                // Телепортация в основном потоке
                Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                    bot.getNPCEntity().teleport(bot.getNPCCurrentLocation().setDirection(
                        targetLocation.toVector().subtract(bot.getNPCCurrentLocation().toVector())));
                });
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

        int breakTime = BREAK_TIME_PER_BLOCK.getOrDefault(targetLocation.getBlock().getType(), 10);

        if (breakProgress < breakTime) {
            breakProgress += 1; // ⚡ Ускоряем в 1 раз
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

                // Добавляем соседние блоки в очередь для добычи
                addAdjacentBlocksToQueue(targetLocation);

                bot.checkAndSelfMove(targetLocation);

                targetLocation = null;
            }
        });
    }

    private Location findNearestTargetBlock(Map<Location, Material> scannedBlocks) {
        Location botLocation = bot.getNPCCurrentLocation();
        Location closestBlock = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Map.Entry<Location, Material> entry : scannedBlocks.entrySet()) {
            if (targetMaterials == null || targetMaterials.contains(entry.getValue())) {
                double distance = botLocation.distanceSquared(entry.getKey());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestBlock = entry.getKey();
                }
            }
        }
        return closestBlock;
    }

    private void addAdjacentBlocksToQueue(Location baseLocation) {
        List<Location> neighbors = Arrays.asList(
            baseLocation.clone().add(1, 0, 0),
            baseLocation.clone().add(-1, 0, 0),
            baseLocation.clone().add(0, 0, 1),
            baseLocation.clone().add(0, 0, -1),
            baseLocation.clone().add(0, -1, 0) // Блоки снизу тоже приоритетны
        );

        for (Location loc : neighbors) {
            if (targetMaterials == null || targetMaterials.contains(loc.getBlock().getType())) {
                pendingBlocks.add(loc);
                BotLogger.debug("➕ Добавили соседний блок в очередь " + BotUtils.formatLocation(loc));
            }
        }
    }
}
