package com.devone.bot.core.logic.task.excavate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.eclipse.jetty.util.StringUtil;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.inventory.BotInventory;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.logic.task.excavate.patterns.IBotExcavatePattern;
import com.devone.bot.core.logic.task.excavate.patterns.generator.BotExcavateInterpretedYamlPattern;
import com.devone.bot.core.logic.task.hand.BotHandTask;
import com.devone.bot.core.logic.task.hand.excavate.BotHandExcavateTask;
import com.devone.bot.core.logic.task.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.core.logic.task.sonar.BotSonar3DTask;
import com.devone.bot.core.zone.BotZoneManager;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BotExcavateTask extends BotTask {

    private int maxBlocks;
    private int outerRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private int innerRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private BotExcavateTaskParams params = new BotExcavateTaskParams();
    private boolean shouldPickup = params.shouldPickup();
    private boolean destroyAllIfNoTarget = false;
    private Set<Material> targetMaterials = null;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private IBotExcavatePattern breakPatternImpl = null;
    private AxisDirection breakDirection = AxisDirection.DOWN;

    private int offsetX, offsetY, offsetZ = 0;

    public BotExcavateTask(Bot bot) {

        super(bot);

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        this.outerRadius = this.params.getOuterRadius();
        this.innerRadius = this.params.getInnerRadius();
        
        this.offsetX     = this.params.getOffsetX();
        this.offsetY     = this.params.getOffsetY();
        this.offsetZ     = this.params.getOffsetZ();

        this.patternName = this.params.getPatternName();
    }

    /**
     * Конфигурирует задачу разрушения.
     * 
     * Параметры (позиционные):
     * 
     * 0 - Set<Material> targetMaterials (nullable) — блоки, которые нужно разрушать.
     * 1 - Integer maxBlocks (nullable) — максимальное количество блоков, которые нужно собрать.
     * 2 - Integer outerRadius (nullable) — радиус разрушения.
     * 3 - Integer innerRadius (nullable) — радиус разрушения.
     * 4 - Boolean shouldPickup (nullable) — собирать ли предметы после разрушения.
     * 5 - Boolean destroyAllIfNoTarget (nullable) — если нет подходящих блоков, разрушать всё подряд.
     * 6 - AxisDirection breakDirection - в какую сторону разрушаем
     * 7 - int offsetX
     * 8 - int offsetY
     * 9 - int offsetZ
     * 10 - IBotDestructionPattern или String (nullable) — шаблон разрушения:
     *     - IBotDestructionPattern — готовый объект.
     *     - String — путь к YAML-файлу шаблона (относительно каталога паттернов).
     *
     * Если параметры не заданы, используются значения по умолчанию.
     */

    @Override
    public BotExcavateTask configure(IBotTaskParams params) {

        super.configure((BotTaskParams)params);
        if(params instanceof BotExcavateTaskParams) {

            BotExcavateTaskParams breakParams = (BotExcavateTaskParams) params;

            this.targetMaterials = breakParams.getTargetMaterials();
            this.maxBlocks = breakParams.getMaxBlocks();
            this.outerRadius = breakParams.getOuterRadius();
            this.innerRadius = breakParams.getInnerRadius();
            this.shouldPickup = breakParams.isShouldPickup();
            this.destroyAllIfNoTarget = breakParams.isDestroyAllIfNoTarget();
            this.breakDirection = breakParams.getBreakDirection();

            this.offsetX = breakParams.getOffsetX();
            this.offsetY = breakParams.getOffsetY();
            this.offsetZ = breakParams.getOffsetZ();

            if (breakParams.getPatternName() != null) {
                this.patternName = breakParams.getPatternName();
                BotLogger.info("📐", isLogging(), "Установлен паттерн разрушения: " + patternName);
            }

        } else {
            BotLogger.info("❌ ", isLogging(), bot.getId() + "Некорректные параметры для `BotBreakTask`!");
        }   

        BotLogger.info("📐", isLogging(), "Установлен паттерн разрушения через config(): " +patternName);

        return this;
    }

    public void setBreakDirection(AxisDirection direction) {
        this.breakDirection = direction;

    }

    public void setOffsetX(int oX) {
        this.offsetX = oX;
    }

    public void setPatterName(String pName) {
        this.patternName = pName;
    }

    public String getPatternName(){
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
        BotLogger.info("🎯", isLogging(), "Установлены целевые блоки: " + materials);
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.info("📜", isLogging(), "Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void execute() {

        BotLogger.info("🚀 ", isLogging(), "Запуск задачи разрушения блоков для бота " + bot.getId() +
                " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");

        if (breakPatternImpl == null) {
            if (!StringUtil.isEmpty(patternName)) {

                Path ptrnPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternName);
                this.breakPatternImpl = new BotExcavateInterpretedYamlPattern(ptrnPath).
                                        configure(offsetX, offsetY, offsetZ, outerRadius, innerRadius, breakDirection);

                BotLogger.info("📐", isLogging(),
                        "ℹ Используется YAML-паттерн: " + this.breakPatternImpl.getName());
                
            } else {
                Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, BotConstants.DEFAULT_PATTERN_BREAK);
                
                this.breakPatternImpl = new BotExcavateInterpretedYamlPattern(fallbackPath).configure(offsetX, offsetY, offsetZ, outerRadius, innerRadius, breakDirection);
                
                BotLogger.info("📐",isLogging(),
                        "Используется дефолтный YAML-паттерн: " + BotConstants.DEFAULT_PATTERN_BREAK);
            }
        }

        if (breakPatternImpl.isFinished()) {
            BotLogger.info("🏁", isLogging(), "Все блоки по паттерну обработаны. Завершаем задачу.");
            this.stop();
            return;
        }

        if (isInventoryFull() || isEnoughBlocksCollected()) {
            BotLogger.info("⛔", isLogging(), "Задача завершена: инвентарь полон или ресурсов достаточно");
            this.stop();
            return;
        }

        bot.pickupNearbyItems(shouldPickup);

        BotLogger.info("🔍", isLogging(), "Запускаем 3D-сканирование окружающей среды.");
        BotSonar3DTask scanTask = new BotSonar3DTask(bot, outerRadius, outerRadius);
        bot.addTaskToQueue(scanTask);


        BotCoordinate3D coordinate = breakPatternImpl.findNextBlock(bot);

        if (coordinate == null) {
            this.stop();
            BotLogger.info("🙈", isLogging(), "Не удалось получить координаты блока для разрушения. Выходим.");
            return;
        }

        BotCoordinate3D targetLocation = new BotCoordinate3D(coordinate.x, coordinate.y, coordinate.z);

        Block targetBlock = BotWorldHelper.getBlockAt(targetLocation);

        bot.getRuntimeStatus().setTargetLocation(targetLocation);

        if (bot.getRuntimeStatus().getTargetLocation() != null) {

            setObjective(params.getObjective() + BotUtils.getBlockName(targetBlock)
                    + " at " + targetLocation);

            if (isInProtectedZone(bot.getRuntimeStatus().getTargetLocation())) {
                BotLogger.info("⛔", isLogging(), bot.getId() + " в запретной зоне, НЕ будет разрушать блок: " +
                        bot.getRuntimeStatus().getTargetLocation());
                this.stop();
                return;
            }

            if (!BotUtils.isBreakableBlock(targetBlock)) {
                BotLogger.info("⛔", isLogging(),"Неразрушаемый блок: "
                        + bot.getRuntimeStatus().getTargetLocation());
                bot.getRuntimeStatus().setTargetLocation(null);
                return;
            }

            Material mat = targetBlock.getType();

            if (BotUtils.requiresTool(mat)) {
                if (!BotInventory.equipRequiredTool(bot, mat)) {
                    BotLogger.info("🙈", isLogging(), "Не удалось взять инструмент в руку. Пропускаем.");
                    bot.getRuntimeStatus().setTargetLocation(null);
                    return;
                }
            }

            setObjective("Excavating: " + BotUtils.getBlockName(targetBlock));

            BotHandTask handTask = new BotHandExcavateTask(bot);
            handTask.configure(new BotHandExcavateTaskParams());
            bot.addTaskToQueue(handTask);

        } else {

            setObjective("The block is not found. ");

            handleNoTargetFound();
        }
    }

    private void handleNoTargetFound() {
        bot.getRuntimeStatus().setTargetLocation(null);

        if (destroyAllIfNoTarget) {
            BotLogger.info("🔄", isLogging(), bot.getId() + " Целевых блоков нет! Запускаем полное разрушение.");
            bot.addTaskToQueue(new BotExcavateAnyAroundTask(bot));
        } else {
            setObjective("");
            BotLogger.info("❌" , isLogging(), bot.getId() + " Нет подходящих блоков. Завершаем.");
            this.stop();
        }
    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
        BotLogger.info("📦", isLogging(), "Проверка инвентаря: " + (full ? "полон" : "есть место"));
        return full;
    }

    private boolean isEnoughBlocksCollected() {
        boolean enough = BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
        BotLogger.info("📊", isLogging(), "Проверка количества блоков: " + (enough ? "достаточно" : "нужно больше"));
        return enough;
    }

    private boolean isInProtectedZone(BotCoordinate3D location) {
        boolean protectedZone = BotZoneManager.getInstance().isInProtectedZone(location);
        if (protectedZone) {
            BotLogger.info("🛑", isLogging(), "Блок в запретной зоне, разрушение запрещено.");
        }
        return protectedZone;
    }

    @Override
    public void stop() {
       this.breakPatternImpl = null;
       bot.getRuntimeStatus().setTargetLocation(null);
       BotLogger.info("🛑", isLogging(), "Задача разрушения остановлена.");
       super.stop();
    }

}
