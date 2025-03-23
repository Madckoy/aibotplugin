package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.core.BotZoneManager;
import com.devone.aibot.core.logic.patterns.destruction.BotAnunakSolidPyramidPattern;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakDefaultPattern;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakInversePyramidPattern;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakRegularHollowPyramidPattern;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakSpiral3DPatternDown;
import com.devone.aibot.core.logic.patterns.destruction.IBotBreakPattern;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.core.logic.tasks.BotTaskSonar3D;
import com.devone.aibot.core.logic.tasks.BotTaskUseHand;
import com.devone.aibot.core.logic.tasks.configs.BotTaskBreakBlockConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class BotTaskBreakBlock extends BotTask {

    private int maxBlocks;
    private int searchRadius;
    private boolean shouldPickup = true;
    private boolean destroyAllIfNoTarget = false;
    private Set<Material> targetMaterials = null;
    private IBotBreakPattern breakPattern = null;

    public BotTaskBreakBlock(Bot bot) {
        super(bot, "⛏️");
        config = new BotTaskBreakBlockConfig();
    }

    @SuppressWarnings("unchecked")
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

        if (params.length >= 6 && params[5] instanceof IBotBreakPattern) {
            this.breakPattern = (IBotBreakPattern) params[5];
        }
        // this.breakPattern = new BotBreakSpiral3DPatternDown(patternRadius);
        // this.breakPattern = new BotBreakLayeredCubePattern(patternRadius);        
        // this.breakPattern = new BotBreakInversePyramidPattern(this.searchRadius); // TESTED OK
        // this.breakPattern = new BotBreakRegularHollowPyramidPattern(this.searchRadius); // TESTED OK
        // this.breakPattern = new BotAnunakSolidPyramidPattern(this.searchRadius); //TESTD OK

        bot.setAutoPickupEnabled(shouldPickup);

        BotLogger.debug("⚙️ BotTaskBreakBlock настроена: " + (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials));
        return this;
    }

    public void setTargetMaterials(Set<Material> materials) {
        this.targetMaterials = materials;
        BotLogger.trace("🎯 Установлены целевые блоки: " + materials);
    }

    public void setBreakPattern(IBotBreakPattern ptrn) {
        breakPattern = ptrn;
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.trace("📜 Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void executeTask() {
        BotLogger.trace("🚀 Запуск задачи разрушения блоков для бота " + bot.getId() + " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");

        if (this.breakPattern == null) {
            this.breakPattern = new BotBreakDefaultPattern();
            this.breakPattern.configure(searchRadius);
        }
        this.breakPattern.configure(searchRadius);

        if (isInventoryFull() || isEnoughBlocksCollected()) {
            BotLogger.trace("⛔ Задача завершена: инвентарь полон или ресурсов достаточно");
            isDone = true;
            bot.getRuntimeStatus().setTargetLocation(null);
            return;
        }

        bot.pickupNearbyItems(shouldPickup);

        if (getGeoMap() == null) {
            BotLogger.trace("🔍 Запускаем 3D-сканирование окружающей среды.");
            BotTaskSonar3D scan_task = new BotTaskSonar3D(bot, this, searchRadius, searchRadius);
            scan_task.configure(scanMode);
            bot.addTaskToQueue(scan_task);
            isDone = false;
            return;
        }

        if(breakPattern.isFinished()) {
            BotLogger.trace("🏁 Все блоки по паттерну обработаны. Завершаем задачу.");
            isDone = true;
            return;
        }

        Location targetLocation = breakPattern.findNextBlock(bot, getGeoMap());

        bot.getRuntimeStatus().setTargetLocation(targetLocation);

        if (targetLocation != null) {

            if (isInProtectedZone(targetLocation)) {
                BotLogger.debug("⛔ " + bot.getId() + " в запретной зоне, НЕ будет разрушать блок: " + BotStringUtils.formatLocation(targetLocation));
                isDone = true;
                bot.getRuntimeStatus().setTargetLocation(null);
                return;
            }

            BotLogger.trace("🛠️ Целевой блок найден: " + BotStringUtils.formatLocation(targetLocation));

            // Проверим, можно ли разрушить в принципе

            if (!BotUtils.isBreakableBlock(targetLocation)) {
                BotLogger.trace("⛔ Неразрушаемый блок: " + BotStringUtils.formatLocation(targetLocation));
                bot.getRuntimeStatus().setTargetLocation(null);
                return;
            }
            
            Material mat = bot.getRuntimeStatus().getTargetLocation().getBlock().getType();

            if(BotUtils.requiresTool(mat)) {

                // Проверить есть ли он у у бота в руке, если нет, то пропускать блок
                if (!BotInventory.equipRequiredTool(bot, mat)) {
                    BotLogger.trace("🙈 Не удалось взять инструмент в руку. Пропускаем.");
                    bot.getRuntimeStatus().setTargetLocation(null);
                    return;
                }

            }

            setObjective("Разрушение блока: " + BotUtils.getBlockName(targetLocation.getBlock()));
            BotLogger.trace("🚧 " + bot.getId() + " Разрушение блока: " + targetLocation.getBlock().toString());

            BotTaskUseHand hand_task = new BotTaskUseHand(bot);
            hand_task.configure(targetLocation);
            bot.addTaskToQueue(hand_task);

        } else {
            handleNoTargetFound();
        }
    }

    private void handleNoTargetFound() {
        bot.getRuntimeStatus().setTargetLocation(null);

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
