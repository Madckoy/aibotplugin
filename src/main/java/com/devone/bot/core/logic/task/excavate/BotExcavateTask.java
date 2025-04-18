package com.devone.bot.core.logic.task.excavate;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.eclipse.jetty.util.StringUtil;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.blocks.BotBlockData;
import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.bot.inventory.BotInventory;
import com.devone.bot.core.logic.task.BotTaskAutoParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.excavate.BotExcavateTask;
import com.devone.bot.core.logic.task.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.logic.task.excavate.patterns.IBotExcavatePattern;
import com.devone.bot.core.logic.task.excavate.patterns.generator.BotExcavateInterpretedYamlPattern;
import com.devone.bot.core.logic.task.hand.excavate.BotHandExcavateTask;
import com.devone.bot.core.logic.task.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.utils.zone.BotZoneManager;

public class BotExcavateTask extends BotTaskAutoParams<BotExcavateTaskParams> {

    private int maxBlocks;
    private int outerRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private int innerRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private Set<Material> targetMaterials = null;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private IBotExcavatePattern breakPatternImpl = null;

    private int offsetX, offsetY, offsetZ = 0;

    public BotExcavateTask(Bot bot) {
        super(bot, BotExcavateTaskParams.class);
    }

    public IBotTaskParameterized<BotExcavateTaskParams> setParams(BotExcavateTaskParams params) {

        this.params = params;

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        this.targetMaterials = params.getTargetMaterials();
        this.maxBlocks = params.getMaxBlocks();
        this.outerRadius = params.getOuterRadius();
        this.innerRadius = params.getInnerRadius();

        this.offsetX = params.getOffsetX();
        this.offsetY = params.getOffsetY();
        this.offsetZ = params.getOffsetZ();

        if (params.getPatternName() != null) {
            this.patternName = params.getPatternName();
            BotLogger.debug("📐", isLogging(), "Установлен паттерн разрушения: " + patternName);
        }

        BotLogger.debug("📐", isLogging(), "Установлен паттерн разрушения через setParams(): " + patternName);

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

    public void setOffsetX(int oX) {
        this.offsetX = oX;
    }

    public void setPatterName(String pName) {
        this.patternName = pName;
    }

    public String getPatternName() {
        return this.patternName;
    }

    public void setOffsetY(int oY) {
        this.offsetY = oY;
    }

    public void setOffsetZ(int oZ) {
        this.offsetZ = oZ;
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

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public int getOffsetZ() {
        return this.offsetZ;
    }

    public void setTargetMaterials(Set<Material> materials) {
        this.targetMaterials = materials;
        BotLogger.debug("🎯", isLogging(), "Установлены целевые блоки: " + materials);
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.debug("📜", isLogging(), "Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void execute() {

        BotLogger.debug("🚀", isLogging(), "Запуск задачи разрушения блоков для бота " + bot.getId() +
                " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");

        if (breakPatternImpl == null) {
            if (!StringUtil.isEmpty(patternName)) {

                Path ptrnPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternName);
                this.breakPatternImpl = new BotExcavateInterpretedYamlPattern(ptrnPath).configure(offsetX, offsetY,
                        offsetZ, outerRadius, innerRadius);

                BotLogger.debug("📐", isLogging(),
                        "ℹ Используется YAML-паттерн: " + this.breakPatternImpl.getName());

            } else {
                Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK,
                        BotConstants.DEFAULT_PATTERN_BREAK);

                this.breakPatternImpl = new BotExcavateInterpretedYamlPattern(fallbackPath).configure(offsetX, offsetY,
                        offsetZ, outerRadius, innerRadius);

                BotLogger.debug("📐", isLogging(),
                        "Используется дефолтный YAML-паттерн: " + BotConstants.DEFAULT_PATTERN_BREAK);
            }
        }

        if (breakPatternImpl.isFinished()) {
            BotLogger.debug("🏁", isLogging(), "Все блоки по паттерну обработаны. Завершаем задачу.");
            this.stop();
            return;
        }

        if(params.isPickup()) {
            if (isInventoryFull() || isEnoughBlocksCollected()) {
                BotLogger.debug("⛔", isLogging(), "Задача завершена: инвентарь полон или ресурсов достаточно");
                this.stop();
                return;
            }
        }

        if(params.isPickup()) {
            bot.pickupNearbyItems();
        }

        BotLocation location = breakPatternImpl.findNextBlock(bot);

        if (location == null) {
            this.stop();
            BotLogger.debug("🙈", isLogging(), "Не удалось получить координаты блока для разрушения. Выходим.");
            return;
        }

        BotLocation targetLocation = new BotLocation(location);

        Block targetBlock = BotWorldHelper.getBlockAt(targetLocation);

        bot.getNavigation().setTarget(targetLocation);

        if (bot.getNavigation().getTarget() != null) {

            setObjective(params.getObjective() + " " + BotUtils.getBlockName(targetBlock)
                    + " at " + targetLocation);

            if (isInProtectedZone(bot.getNavigation().getTarget())) {
                BotLogger.debug("⛔", isLogging(), bot.getId() + " в запретной зоне, НЕ будет разрушать блок: " +
                        bot.getNavigation().getTarget());
                this.stop();
                return;
            }

            if (!BotUtils.isBreakableBlock(targetBlock)) {
                BotLogger.debug("⛔", isLogging(), "Неразрушаемый блок: "
                        + bot.getNavigation().getTarget());
                bot.getNavigation().setTarget(null);
                return;
            }

            Material mat = targetBlock.getType();

            if (BotUtils.requiresTool(mat)) {
                if (!BotInventory.equipRequiredTool(bot, mat)) {
                    BotLogger.debug("🙈", isLogging(), "Не удалось взять инструмент в руку. Пропускаем.");
                    bot.getNavigation().setTarget(null);
                    return;
                }
            }

            
            BotBlockData block = BotWorldHelper.worldBlockToBotBlock(targetBlock);
            BotHandExcavateTask handTask = new BotHandExcavateTask(bot);
            BotHandExcavateTaskParams params = new BotHandExcavateTaskParams();
            params.setTarget(block);
            handTask.setParams(params);
            bot.getLifeCycle().getTaskStackManager().pushTask(handTask);

        } else {

            setObjective("The block is not found. ");

            handleNoTargetFound();
        }
    }

    private void handleNoTargetFound() {
        bot.getNavigation().setTarget(null);

        setObjective("");
        BotLogger.debug("❌", isLogging(), bot.getId() + " Нет подходящих блоков. Завершаем.");
        this.stop();

    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
        BotLogger.debug("📦", isLogging(), "Проверка инвентаря: " + (full ? "полон" : "есть место"));
        return full;
    }

    private boolean isEnoughBlocksCollected() {
        boolean enough = BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
        BotLogger.debug("📊", isLogging(), "Проверка количества блоков: " + (enough ? "достаточно" : "нужно больше"));
        return enough;
    }

    private boolean isInProtectedZone(BotLocation location) {
        boolean protectedZone = BotZoneManager.getInstance().isInProtectedZone(location);
        if (protectedZone) {
            BotLogger.debug("🛑", isLogging(), "Блок в запретной зоне, разрушение запрещено.");
        }
        return protectedZone;
    }

    @Override
    public void stop() {
        this.breakPatternImpl = null;
        bot.getNavigation().setTarget(null);
        BotLogger.debug("🛑", isLogging(), "Задача разрушения остановлена.");
        super.stop();
    }

}