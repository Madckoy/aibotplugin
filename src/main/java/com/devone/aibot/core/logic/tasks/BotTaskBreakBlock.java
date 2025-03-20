package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.BotInventory;
import java.util.*;

import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotEnv3DScan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private BotTaskBreakBlockConfig config;
    private Location targetLocation;

    public BotTaskBreakBlock(Bot bot) {
        super(bot, "⛏️");
        this.config = new BotTaskBreakBlockConfig();
        isEnabled = config.isEnabled();
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
        if (params.length >= 4 && params[3] instanceof Boolean) {
            this.shouldPickup = (Boolean) params[3];
        }
        if (params.length >= 5 && params[4] instanceof Boolean) {
            this.destroyAllIfNoTarget = (Boolean) params[4];
        }
        bot.setAutoPickupEnabled(shouldPickup);
        BotLogger.debug("⚙️ BreakBlockTask настроена: " + (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials));
    }

    public void setTargetMaterials(Set<Material> materials) {
        this.targetMaterials = materials;
    }

    public Set<Material> getTargetMaterials() {
        return this.targetMaterials;
    }

    @Override
    public void executeTask() {
        if (isInventoryFull() || isEnoughBlocksCollected()) {
            isDone = true;
            return;
        }

        bot.pickupNearbyItems(shouldPickup);

        if (getEnvMap() == null) {
            bot.addTaskToQueue(new BotTaskSonar3D(bot, this, searchRadius, 4));
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
            destroyBlock(targetLocation);
        } else {
            handleNoTargetFound();
        }
    }

    private boolean isInventoryFull() {
        return !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
    }

    private boolean isEnoughBlocksCollected() {
        return BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
    }

    private Location findNextTargetBlock() {
        return BotEnv3DScan.getRandomNearbyDestructibleBlock(getEnvMap(), bot.getNPCCurrentLocation());
    }

    private void destroyBlock(Location target) {
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            if (target.getBlock().getType() != Material.AIR) {
                target.getBlock().breakNaturally();
                BotLogger.debug("✅ Блок разрушен на " + BotStringUtils.formatLocation(target));
                isDone = false;
            }
        });
    }

    private void handleNoTargetFound() {
        if (destroyAllIfNoTarget) {
            BotLogger.trace("🔄 " + bot.getId() + " Целевых блоков нет! Запускаем полное разрушение.");
            bot.addTaskToQueue(new BotTaskBreakBlockAny(bot));
            isDone = false;
        } else {
            BotLogger.trace("❌ " + bot.getId() + " Нет подходящих блоков. Завершаем.");
            isDone = true;
        }
    }

    private boolean isInProtectedZone(Location location) {
        return BotZoneManager.getInstance().isInProtectedZone(location);
    }
}
