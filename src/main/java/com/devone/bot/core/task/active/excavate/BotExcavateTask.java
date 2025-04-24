package com.devone.bot.core.task.active.excavate;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.eclipse.jetty.util.StringUtil;

import com.devone.bot.core.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.task.active.excavate.patterns.IBotExcavatePatternRunner;
import com.devone.bot.core.task.active.excavate.patterns.generator.BotExcavateTemplateRunner;
import com.devone.bot.core.task.active.excavate.patterns.generator.params.BotExcavateTemplateRunnerParams;
import com.devone.bot.core.task.active.hand.excavate.BotHandExcavateTask;
import com.devone.bot.core.task.active.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.utils.zone.BotZoneManager;

public class BotExcavateTask extends BotTaskAutoParams<BotExcavateTaskParams> {

    private int maxBlocks;
    private int outerRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private int innerRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private Set<Material> targetMaterials = null;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private IBotExcavatePatternRunner patternRunner = null;
    private BotExcavateTemplateRunnerParams excavateParams = new BotExcavateTemplateRunnerParams();

    public BotExcavateTask(Bot bot) {
        super(bot, BotExcavateTaskParams.class);
    }

    public IBotTaskParameterized<BotExcavateTaskParams> setParams(BotExcavateTaskParams params) {

        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        if (params.getPatternName() != null) {
            this.patternName = params.getPatternName();
            BotLogger.debug(icon, isLogging(), bot.getId()+ " 📐 Установлен паттерн разрушения: " + patternName);
        }

        BotLogger.debug(icon, isLogging(), bot.getId() + " 📐 Установлен паттерн разрушения через setParams(): " + patternName);

        return this;
    }

    /**
     * Конфигурирует задачу разрушения.
     * 
     * Параметры (позиционные):
     * 
     * 0 - Set<Material> targetMaterials (nullable) — блоки, которые нужно
     * разрушать.
     * 1 - Integer maxBlocks (nullable) — максимальное количество блоков, которые
     * нужно собрать.
     * 2 - Integer outerRadius (nullable) — радиус разрушения.
     * 3 - Integer innerRadius (nullable) — радиус разрушения.
     * 4 - Boolean shouldPickup (nullable) — собирать ли предметы после разрушения.
     * 5 - Boolean destroyAllIfNoTarget (nullable) — если нет подходящих блоков,
     * разрушать всё подряд.
     * 7 - int offsetX
     * 8 - int offsetY
     * 9 - int offsetZ
     * 10 - IBotDestructionPattern или String (nullable) — шаблон разрушения:
     * - IBotDestructionPattern — готовый объект.
     * - String — путь к YAML-файлу шаблона (относительно каталога паттернов).
     *
     * Если параметры не заданы, используются значения по умолчанию.
     */

    public String getPatternName() {
        return this.patternName;
    }


    public int getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(int r) {
        this.outerRadius = r;
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(int r) {
        this.innerRadius = r;
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

        // 🚨 Проверка на опасную жидкость
        if (BotWorldHelper.isInDangerousLiquid(bot)) {
            BotLogger.debug(icon, isLogging(), bot.getId() + " 💧 Оказался в опасной жидкости. Завершаем копку.");
            this.stop();
            return;
        }

        if (patternRunner == null) {
            if (!StringUtil.isEmpty(patternName)) {

                Path ptrnPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternName);

                this.patternRunner = new BotExcavateTemplateRunner(ptrnPath).init(bot.getNavigator().getPosition().getX(),
                                                                                  bot.getNavigator().getPosition().getY(),
                                                                                  bot.getNavigator().getPosition().getZ());

                //setParams(null ); //null because we read from the template file

                BotLogger.debug(icon, isLogging(), bot.getId() +
                        " 📐 Используется YAML-паттерн: " + this.patternRunner.getName());

            } else {
                
                Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK,
                        BotConstants.DEFAULT_PATTERN_BREAK);

                this.patternRunner = new BotExcavateTemplateRunner(fallbackPath).setParams( excavateParams );

                BotLogger.debug(icon, isLogging(), bot.getId() +
                        " 📐 Используется дефолтный YAML-паттерн: " + BotConstants.DEFAULT_PATTERN_BREAK);
            }
        }

        if (patternRunner.isFinished()) {
            BotLogger.debug(icon, isLogging(), " 🏁 Все блоки по паттерну обработаны. Завершаем задачу.");
            this.stop();
            return;
        }

        if (params.isPickup()) {
            if (isInventoryFull() || isEnoughBlocksCollected()) {
                BotLogger.debug(icon, isLogging(),
                        bot.getId() + " ⛔ Задача завершена: инвентарь полон или ресурсов достаточно");
                this.stop();
                return;
            }
        }

        if (params.isPickup()) {
            bot.pickupNearbyItems();
        }
        // -----------------
        BotPosition location = patternRunner.getNextBlock(bot);
        // -----------------
        if (location == null) {
            this.stop();
            BotLogger.debug(icon, isLogging(),
                    bot.getId() + " 🙈 Не удалось получить координаты блока для разрушения. Выходим.");
            return;
        }

        BotPosition targetLocation = new BotPosition(location);

        Block targetBlock = BotWorldHelper.botPositionToWorldBlock(targetLocation);

        bot.getNavigator().setTarget(targetLocation);

        turnToTarget(this, targetLocation);

        if (bot.getNavigator().getPoi() != null) {

            setObjective(params.getObjective() + " " + BotUtils.getBlockName(targetBlock)
                    + " at " + targetLocation);

            if (isInProtectedZone(bot.getNavigator().getPoi())) {
                BotLogger.debug(icon, isLogging(), bot.getId() + " ⛔ в запретной зоне, НЕ будет разрушать блок: " +
                        bot.getNavigator().getPoi());
                        
                this.stop();
                return;
            }

            if (!BotWorldHelper.isBreakableBlock(targetBlock)) {

                BotLogger.debug(icon, isLogging(), bot.getId() + " ⛔ Неразрушаемый блок: "
                        + bot.getNavigator().getPoi());

                bot.getNavigator().setTarget(null);
                
                return;
            }

            Material mat = targetBlock.getType();

            if (BotUtils.requiresTool(mat)) {
                if (!BotInventory.equipRequiredTool(bot, mat)) {
                    BotLogger.debug(icon, isLogging(),
                            bot.getId() + " 🙈 Не удалось взять инструмент в руку. Пропускаем.");
                    bot.getNavigator().setTarget(null);
                    return;
                }
            }

            BotBlockData block = BotWorldHelper.blockToBotBlockData(targetBlock);
            BotHandExcavateTask handTask = new BotHandExcavateTask(bot);
            BotHandExcavateTaskParams params = new BotHandExcavateTaskParams();
            params.setTarget(block);
            handTask.setParams(params);
            BotTaskManager.push(bot, handTask);

        } else {

            setObjective("The block is not found. ");

            handleNoTargetFound();
        }
    }

    private void handleNoTargetFound() {
        bot.getNavigator().setTarget(null);

        setObjective("");
        BotLogger.debug(icon, isLogging(), bot.getId() + bot.getId() + " ❌ Нет подходящих блоков. Завершаем.");
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
        this.patternRunner = null;
        bot.getNavigator().setTarget(null);
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🛑 Задача разрушения остановлена.");
        super.stop();
    }

}