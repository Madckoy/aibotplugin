package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.BotInventory;
import java.util.*;

import com.devone.aibot.utils.BotStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotZoneManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotScanEnv;
import com.devone.aibot.AIBotPlugin;

public class BotTaskBreakBlock extends BotTask {

    private int maxBlocks;
    private int searchRadius;
    private int breakProgress = 0;
    private Set<Material> targetMaterials = null;

    private Map<Location, Material> scannedBlocks;

    private boolean shouldPickup = true;

    private Queue<Location> pendingBlocks = new LinkedList<>(); // Очередь блоков рядом

    private static final Map<Material, Integer> BREAK_TIME_PER_BLOCK = new HashMap<>();
    
    public BotTaskBreakBlock(Bot bot) {
        super(bot, "BREAK");
        this.bot = bot;
    }
    
    @Override
    public void configure(Object... params) {
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
        
        if (params.length >= 4 && params[3] instanceof Integer) {
            this.shouldPickup = (boolean) params[3];
        }

        bot.setAutoPickupEnabled(shouldPickup);

        BotLogger.info("🔨 BreakBlockTask сконфигурирована: " + (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials));
    }
    
    @Override
    public void executeTask() {

        if(!BotInventory.hasFreeInventorySpace(bot, targetMaterials)) {
            BotLogger.info("🔄 " +bot.getId() + " No free space in Inventory! Exiting...");
            isDone = true;
            return;
        }

        //
        if (BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks)){
            BotLogger.info("🔄 " + bot.getId() + " Collected enough materials! Exiting...");
            isDone = true;
            return;
        }

        // pickup items
        bot.pickupNearbyItems(shouldPickup);

        if (targetLocation == null) {
            if (!pendingBlocks.isEmpty()) {
                targetLocation = pendingBlocks.poll(); // Берем следующий блок из очереди
                BotLogger.info(" 🔄 " + bot.getId() + " Переход к следующему блоку " + BotStringUtils.formatLocation(targetLocation));
            } else {
                // Получаем карту блоков в радиусе поиска
                Map<Location, Material> scannedBlocks = BotScanEnv.scan3D(bot.getNPCCurrentLocation(), searchRadius);

                targetLocation = findNearestTargetBlock(scannedBlocks);

                if (targetLocation == null) {
                    BotLogger.info("❌ " + bot.getId() + " Нет доступных блоков для добычи! Перемещаемся к новой цели.");
                                               
                        Location newLocation = findNearestTargetBlock(scannedBlocks);

                        if(newLocation!=null) {

                            BotTaskMove moveTask = new BotTaskMove(bot);
                            moveTask.configure(newLocation);
                      
                            bot.getLifeCycle().getTaskStackManager().pushTask(moveTask); // Перемещаем бота через новый таск в стеке

                           return;

                        } else {
                            //  Stop Task and exit
                            isDone = true;
                            return;
                        }
                }

                BotLogger.info("🛠️ " + bot.getId() + " Нашел " + targetLocation.getBlock().getType() + " на " + BotStringUtils.formatLocation(targetLocation));

                // Телепортация в основном потоке
                Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                    bot.getNPCEntity().teleport(bot.getNPCCurrentLocation().setDirection(
                        targetLocation.toVector().subtract(bot.getNPCCurrentLocation().toVector())));
                });
            }
        }

        if (targetLocation.getBlock().getType() == Material.AIR) {
            BotLogger.info("⚠️ " + bot.getId() + " Бот пытается ломать воздух! Меняем цель...");
            targetLocation = null;
            return;
        }

        if (BotZoneManager.getInstance().isInProtectedZone(targetLocation)) {
            BotLogger.info("⛔ " + bot.getId() + " в запретной зоне, НЕ будет разрушать блок: " + BotStringUtils.formatLocation(targetLocation));
            isDone = true;
            return;
        }

        int breakTime = BREAK_TIME_PER_BLOCK.getOrDefault(targetLocation.getBlock().getType(), 10);

        if (breakProgress < breakTime) {
            breakProgress += 1; // ⚡ Ускоряем в 1 раз
            bot.getNPCEntity().getWorld().playEffect(targetLocation, org.bukkit.Effect.STEP_SOUND, targetLocation.getBlock().getType());
            BotLogger.info("⏳ "+ bot.getId() + " Ломает " + targetLocation.getBlock().getType() + " [" + breakProgress + "/" + breakTime + "]");
            return;
        }

        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            if (targetLocation != null && targetLocation.getBlock().getType() != Material.AIR) {
                targetLocation.getBlock().breakNaturally();
                BotLogger.info("✅ Блок разрушен на " + BotStringUtils.formatLocation(targetLocation));

                // check inventory here

                //blocksMined++;
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
                BotLogger.info("➕ Добавили соседний блок в очередь " + BotStringUtils.formatLocation(loc));
            }
        }
    }

}
