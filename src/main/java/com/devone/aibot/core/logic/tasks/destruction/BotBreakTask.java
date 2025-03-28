package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.core.BotZoneManager;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakInterpretedYamlPattern;
import com.devone.aibot.core.logic.patterns.destruction.IBotDestructionPattern;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.core.logic.tasks.BotSonar3DTask;
import com.devone.aibot.core.logic.tasks.BotUseHandTask;
import com.devone.aibot.core.logic.tasks.configs.BotBreakBlockTaskConfig;
import com.devone.aibot.utils.Bot3DCoordinate;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BotBreakTask extends BotTask {

    private int maxBlocks;
    private int breakRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private boolean shouldPickup = true;
    private boolean destroyAllIfNoTarget = false;
    private Set<Material> targetMaterials = null;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private IBotDestructionPattern breakPattern = null;

    private AxisDirection direction = AxisDirection.DOWN;

    public BotBreakTask(Bot bot) {

        super(bot, "🪨👁🧑‍🔧");

        this.config = new BotBreakBlockTaskConfig();

        breakRadius = this.config.getBreakRadius();

        this.patternName = ((BotBreakBlockTaskConfig) config).getPattern();

        Path path = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternName);

        this.breakPattern = new BotBreakInterpretedYamlPattern(path).configure(breakRadius, direction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BotTask configure(Object... params) {
        super.configure(params);
        if (params.length >= 1 && params[0] instanceof Set) {
            targetMaterials = (Set<Material>) params[0];
            if (targetMaterials.isEmpty())
                targetMaterials = null;
        }
        if (params.length >= 2 && params[1] instanceof Integer) {
            this.maxBlocks = (Integer) params[1];
        }
        if (params.length >= 3 && params[2] instanceof Integer) {
            this.breakRadius = (Integer) params[2];
        }
        if (params.length >= 4 && params[3] instanceof Boolean) {
            this.shouldPickup = (Boolean) params[3];
        }
        if (params.length >= 5 && params[4] instanceof Boolean) {
            this.destroyAllIfNoTarget = (Boolean) params[4];
        }

        // YAML-паттерн через параметры
        if (params.length >= 7 && params[6] instanceof String patternFile && patternFile.endsWith(".yml")) {

            Path path = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternFile);

            this.breakPattern = new BotBreakInterpretedYamlPattern(path).configure(breakRadius, direction);

            BotLogger.info(isLogging(), "ℹ️ 📐 Загружен YAML-паттерн: " + patternFile);
        }

        // Если не задано — fallback на default.yml
        if (this.breakPattern == null) {
            Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, BotConstants.DEFAULT_PATTERN_BREAK);
            this.breakPattern = new BotBreakInterpretedYamlPattern(fallbackPath).configure(breakRadius, direction);
            BotLogger.info(isLogging(),
                    "ℹ️ 📐 Используется дефолтный YAML-паттерн: " + BotConstants.DEFAULT_PATTERN_BREAK);
        }

        BotLogger.info(isLogging(), "📐 Выбран паттерн разрушения: " + breakPattern.getName());

        bot.setAutoPickupEnabled(shouldPickup);

        BotLogger.debug(isLogging(),
                "⚙️ BotTaskBreakBlock настроена: " + (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials));
        return this;
    }

    public void setDirection(AxisDirection direction) {
        this.direction = direction;

    }

    public int getBreakRadius() {
        return breakRadius;
    }

    public void setTargetMaterials(Set<Material> materials) {
        this.targetMaterials = materials;
        BotLogger.trace(isLogging(), "🎯 Установлены целевые блоки: " + materials);
    }

    public void setBreakPattern(IBotDestructionPattern ptrn) {
        breakPattern = ptrn;
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.trace(isLogging(), "📜 Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void executeTask() {

        BotLogger.trace(isLogging(), "🚀 Запуск задачи разрушения блоков для бота " + bot.getId() +
                " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");

        if (breakPattern == null) {
            Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, BotConstants.DEFAULT_PATTERN_BREAK);
            this.breakPattern = new BotBreakInterpretedYamlPattern(fallbackPath).configure(breakRadius, direction);
            BotLogger.info(isLogging(),
                    "ℹ️ 📐 Используется дефолтный YAML-паттерн: " + BotConstants.DEFAULT_PATTERN_BREAK);
        }

        if (isInventoryFull() || isEnoughBlocksCollected()) {
            BotLogger.trace(isLogging(), "⛔ Задача завершена: инвентарь полон или ресурсов достаточно");
            isDone = true;
            bot.getRuntimeStatus().setTargetLocation(null);
            return;
        }

        bot.pickupNearbyItems(shouldPickup);

        if (getGeoMap() == null) {
            BotLogger.trace(isLogging(), "🔍 Запускаем 3D-сканирование окружающей среды.");
            BotSonar3DTask scanTask = new BotSonar3DTask(bot, this, breakRadius, breakRadius);
            scanTask.configure(scanMode);
            bot.addTaskToQueue(scanTask);
            isDone = false;
            return;
        }

        if (breakPattern.isFinished()) {
            BotLogger.trace(isLogging(), "🏁 Все блоки по паттерну обработаны. Завершаем задачу.");
            isDone = true;
            return;
        }

        Bot3DCoordinate coordinate = breakPattern.findNextBlock(bot);

        if (coordinate == null) {
            return;
        }

        Location targetLocation = new Location(Bukkit.getWorlds().get(0), coordinate.x, coordinate.y, coordinate.z);

        bot.getRuntimeStatus().setTargetLocation(targetLocation);

        if (bot.getRuntimeStatus().getTargetLocation() != null) {

            setObjective("Probing: " + BotUtils.getBlockName(bot.getRuntimeStatus().getTargetLocation().getBlock())
                    + " at " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));

            if (isInProtectedZone(bot.getRuntimeStatus().getTargetLocation())) {
                BotLogger.debug(isLogging(), "⛔ " + bot.getId() + " в запретной зоне, НЕ будет разрушать блок: " +
                        BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));
                isDone = true;
                bot.getRuntimeStatus().setTargetLocation(null);
                return;
            }

            if (!BotUtils.isBreakableBlock(bot.getRuntimeStatus().getTargetLocation())) {
                BotLogger.trace(isLogging(), "⛔ Неразрушаемый блок: "
                        + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));
                bot.getRuntimeStatus().setTargetLocation(null);
                return;
            }

            Material mat = bot.getRuntimeStatus().getTargetLocation().getBlock().getType();

            if (BotUtils.requiresTool(mat)) {
                if (!BotInventory.equipRequiredTool(bot, mat)) {
                    BotLogger.trace(isLogging(), "🙈 Не удалось взять инструмент в руку. Пропускаем.");
                    bot.getRuntimeStatus().setTargetLocation(null);
                    return;
                }
            }

            setObjective("Breaking: " + BotUtils.getBlockName(bot.getRuntimeStatus().getTargetLocation().getBlock()));

            // BotLogger.trace("🚧 " + bot.getId() + " Разрушение блока: " +
            // targetLocation.getBlock().toString());

            BotUseHandTask handTask = new BotUseHandTask(bot, "🪨⛏🧑‍🔧");
            handTask.configure(targetLocation);
            bot.addTaskToQueue(handTask);

        } else {

            setObjective("The block is not found. ");

            handleNoTargetFound();
        }
    }

    private void handleNoTargetFound() {
        bot.getRuntimeStatus().setTargetLocation(null);

        if (destroyAllIfNoTarget) {
            BotLogger.trace(isLogging(), "🔄 " + bot.getId() + " Целевых блоков нет! Запускаем полное разрушение.");
            bot.addTaskToQueue(new BotBreakAnyTask(bot));
            isDone = false;
        } else {
            setObjective("");
            BotLogger.trace(isLogging(), "❌ " + bot.getId() + " Нет подходящих блоков. Завершаем.");
            isDone = true;
        }
    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
        BotLogger.trace(isLogging(), "📦 Проверка инвентаря: " + (full ? "полон" : "есть место"));
        return full;
    }

    private boolean isEnoughBlocksCollected() {
        boolean enough = BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
        BotLogger.trace(isLogging(), "📊 Проверка количества блоков: " + (enough ? "достаточно" : "нужно больше"));
        return enough;
    }

    private boolean isInProtectedZone(Location location) {
        boolean protectedZone = BotZoneManager.getInstance().isInProtectedZone(location);
        if (protectedZone) {
            BotLogger.trace(isLogging(), "🛑 Блок в запретной зоне, разрушение запрещено.");
        }
        return protectedZone;
    }
}
