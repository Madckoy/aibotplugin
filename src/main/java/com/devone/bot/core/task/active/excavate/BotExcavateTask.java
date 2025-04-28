package com.devone.bot.core.task.active.excavate;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import com.devone.bot.core.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.task.active.hand.excavate.BotHandExcavateTask;
import com.devone.bot.core.task.active.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.pattern.BotPatternRunner;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.utils.zone.BotZoneManager;

public class BotExcavateTask extends BotTaskAutoParams<BotExcavateTaskParams> {

    private int maxBlocks;
    private Set<Material> targetMaterials = null;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private BotPosition basePosition;
    private long blocksCounter = 0;
    private BotPatternRunner runner = null;

    public BotExcavateTask(Bot bot) {
        super(bot, BotExcavateTaskParams.class);
    }

    public IBotTaskParameterized<BotExcavateTaskParams> setParams(BotExcavateTaskParams params) {

        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());
        setEnabled(params.isEnabled());

        if (params.getPatternName() != null) {
            this.patternName = params.getPatternName();
            BotLogger.debug(icon, isLogging(), bot.getId() + " 📐 Установлен паттерн разрушения: " + patternName);
        }

        BotLogger.debug(icon, isLogging(),
                bot.getId() + " 📐 Установлен паттерн разрушения через setParams(): " + patternName);

        return this;
    }

    public String getPatternName() {
        return this.patternName;
    }

    public void setTargetMaterials(Set<Material> materials) {
        this.targetMaterials = materials;
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🎯 Установлены целевые блоки: " + materials);
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.debug(icon, isLogging(), bot.getId() + " 📜 Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void execute() {

        BotLogger.debug(icon, isLogging(), bot.getId() + " 🚀 Запуск задачи разрушения блоков для бота " + bot.getId() +
                " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");

        basePosition = new BotPosition(bot.getNavigator().getPosition());

        if (runner == null) {
            runner = new BotPatternRunner();
        }

        // 🚨 Проверка на опасную жидкость
        if (BotWorldHelper.isInDangerousLiquid(bot)) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 💧 Оказался в опасной жидкости. Завершаем копку.");
            this.stop();
            return;
        }

        BotPosition blockPosition = null;

        setIcon("📜");

        setObjective(params.getObjective() + ": Processing pattern - " + patternName);

        if (!runner.isLoaded()) {
            try {
                runner.load(basePosition);
            } catch (Exception ex) {
                BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Ошибка загрузки паттерна: " + ex.getMessage());
                return; // exit and go to another cycle
            }
        }

        blockPosition = runner.getNextVoid(basePosition);
        BotLogger.debug(icon, isLogging(), bot.getId() + " Next Void: " + blockPosition);

        if (runner.isNoVoid()) {
            BotLogger.debug(icon, isLogging(), " 🏁 Все блоки по паттерну обработаны. Завершаем задачу.");
            this.stop();
            return;
        } else {
            if (params.isPickup()) {
                bot.pickupNearbyItems();
                if (isInventoryFull() || isEnoughBlocksCollected()) {
                    BotLogger.debug(icon, isLogging(),
                            bot.getId() + " ⛔ Задача завершена: инвентарь полон или ресурсов достаточно");
                    this.stop();
                    return;
                }
            }

            if (blockPosition != null) {
                Block targetBlock = BotWorldHelper.botPositionToWorldBlock(blockPosition);
                bot.getNavigator().setPoi(blockPosition);

                if (targetBlock.getType().toString().equals(Material.AIR.toString()) ||
                        targetBlock.getType().toString().equals(Material.CAVE_AIR.toString()) ||
                        targetBlock.getType().toString().equals(Material.VOID_AIR.toString()) ||
                        targetBlock.getType().toString().equals(Material.WATER.toString()) ||
                        targetBlock.getType().toString().equals(Material.LAVA.toString())) {

                    BotLogger.debug(icon, isLogging(), bot.getId() + " Блок не разрушимый или уже разрушен: "
                            + blockPosition + " " + targetBlock.getType());
                    return;
                }

                BotLogger.debug(icon, isLogging(),
                        bot.getId() + " Поворачивает голову в сторону: " + blockPosition + " " + targetBlock.getType());
                turnToTarget(this, blockPosition);

                if (bot.getNavigator().getPoi() != null) {
                    setObjective(
                            params.getObjective() + " " + BotUtils.getBlockName(targetBlock) + " at " + blockPosition);

                    if (isInProtectedZone(bot.getNavigator().getPoi())) {
                        BotLogger.debug(icon, isLogging(),
                                bot.getId() + " ⛔ в запретной зоне, НЕ будет разрушать блок: "
                                        + bot.getNavigator().getPoi());
                        return;
                    }
                }

                if (BotWorldHelper.isBreakableBlock(targetBlock) == false) {

                    BotLogger.debug(icon, isLogging(),
                            bot.getId() + " ⛔ Неразрушаемый блок: " + bot.getNavigator().getPoi());

                    bot.getNavigator().setPoi(null);
                    return;
                }

                Material mat = targetBlock.getType();

                if (BotUtils.requiresTool(mat)) {
                    if (!BotInventory.equipRequiredTool(bot, mat)) {
                        BotLogger.debug(icon, isLogging(),
                                bot.getId() + " 🙈 Не удалось взять инструмент в руку. Пропускаем.");
                        bot.getNavigator().setPoi(null);
                        return;
                    }
                }

                this.setPause(true);

                BotBlockData block = BotWorldHelper.blockToBotBlockData(targetBlock);
                BotHandExcavateTask handTask = new BotHandExcavateTask(bot);
                BotHandExcavateTaskParams params = new BotHandExcavateTaskParams();
                params.setTarget(block);
                handTask.setParams(params);
                BotTaskManager.push(bot, handTask);

                BotLogger.info(this.getIcon(), isLogging(), bot.getId() + "  Blocks processed: " + blocksCounter);

            } else {

                setObjective("The block is not found. ");
                handleNoTargetFound();
            }
        }
        return;
    }

    private void handleNoTargetFound() {
        bot.getNavigator().setPoi(null);

        setObjective("");
        BotLogger.debug(icon, isLogging(), bot.getId() + " ❌ Нет подходящих блоков. Завершаем.");
        this.stop();

    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
        BotLogger.debug(icon, isLogging(), bot.getId() + " 📦 Проверка инвентаря: " + (full ? "полон" : "есть место"));
        return full;
    }

    private boolean isEnoughBlocksCollected() {
        boolean enough = BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
        BotLogger.debug(icon, isLogging(),
                bot.getId() + " 📊 Проверка количества блоков: " + (enough ? "достаточно" : "нужно больше"));
        return enough;
    }

    private boolean isInProtectedZone(BotPosition location) {
        boolean protectedZone = BotZoneManager.getInstance().isInProtectedZone(location);
        if (protectedZone) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 🛑 Блок в запретной зоне, разрушение запрещено.");
        }
        return protectedZone;
    }

    @Override
    public void stop() {
        this.runner = null;
        bot.getNavigator().setPoi(null);
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🛑 Задача разрушения остановлена.");
        super.stop();
    }

}