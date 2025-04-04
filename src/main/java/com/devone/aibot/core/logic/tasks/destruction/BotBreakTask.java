package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.core.BotZoneManager;
import com.devone.aibot.core.logic.patterns.destruction.BotBreakInterpretedYamlPattern;
import com.devone.aibot.core.logic.patterns.destruction.IBotDestructionPattern;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.core.logic.tasks.BotSonar3DTask;
import com.devone.aibot.core.logic.tasks.BotUseHandTask;
import com.devone.aibot.core.logic.tasks.configs.BotBreakTaskConfig;
import com.devone.aibot.utils.BotCoordinate3D;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.eclipse.jetty.util.StringUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BotBreakTask extends BotTask {

    private int maxBlocks;
    private int outerRadius = BotConstants.DEFAULT_SCAN_RANGE;
    private int innerRadius = BotConstants.DEFAULT_SCAN_RANGE;

    private boolean shouldPickup = true;
    private boolean destroyAllIfNoTarget = false;
    private Set<Material> targetMaterials = null;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private IBotDestructionPattern breakPattern = null;
    private AxisDirection breakDirection = AxisDirection.DOWN;

    private int offsetX, offsetY, offsetZ = 0;

    public BotBreakTask(Bot bot) {

        super(bot, "🪨👁");

        this.config = new BotBreakTaskConfig();
        this.isLogged = config.isLogged();

        this.outerRadius = ((BotBreakTaskConfig)this.config).getOuterRadius();
        this.innerRadius = ((BotBreakTaskConfig)this.config).getInnerRadius();
        
        this.offsetX     = ((BotBreakTaskConfig)this.config).getOffsetX();
        this.offsetY     = ((BotBreakTaskConfig)this.config).getOffsetY();
        this.offsetZ     = ((BotBreakTaskConfig)this.config).getOffsetZ();

        this.patternName = ((BotBreakTaskConfig) config).getPattern();

        Path path = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternName);

        //this.breakPattern = new BotBreakInterpretedYamlPattern(path).configure(offsetX,offsetY, offsetZ,  breakRadius, breakRadius, AxisDirection.DOWN);

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

    @SuppressWarnings("unchecked")
    @Override
    public BotTask configure(Object... params) {

        BotLogger.info(this.isLogged(), "⚙️ Запуск configure() с параметрами: " + Arrays.toString(params));


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
            this.outerRadius = (Integer) params[2];
        }
        if (params.length >= 4 && params[3] instanceof Integer) {
            this.innerRadius = (Integer) params[3];
        }

        if (params.length >= 5 && params[4] instanceof Boolean) {
            this.shouldPickup = (Boolean) params[4];
        }
        if (params.length >= 6 && params[5] instanceof Boolean) {
            this.destroyAllIfNoTarget = (Boolean) params[5];
        }
        
        if (params.length >= 7 && params[6] instanceof AxisDirection bd) {
            this.breakDirection  = bd;
        }
        if (params.length >= 8 && params[7] instanceof Integer) {
            this.offsetX = (Integer) params[7];
        }
        if (params.length >= 9 && params[8] instanceof Integer) {
            this.offsetY = (Integer) params[8];
        }
        if (params.length >= 10 && params[9] instanceof Integer) {
            this.offsetZ = (Integer) params[9];
        }
        // Применяем создание шаблона с новыми параметрами
        if (params.length >= 11) {
            if (params[10] instanceof IBotDestructionPattern ptrn) {
                this.breakPattern = ptrn;
                BotLogger.info(this.isLogged(), "ℹ️ 📐 Получен готовый YAML-паттерн через параметры: " + ptrn.getName());
            } else if (params[10] instanceof String patternFile && patternFile.endsWith(".yml")) {
                Path path = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternFile);

                this.breakPattern = new BotBreakInterpretedYamlPattern(path)
                        .configure(offsetX, offsetY, offsetZ, outerRadius, innerRadius, AxisDirection.DOWN);

                BotLogger.info(this.isLogged(), "ℹ️ 📐 Загружен YAML-паттерн по имени: " + patternFile);
            }
        }

        // Если не задано — fallback на default.yml
        if (this.breakPattern == null ) {
            Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternName);
            this.breakPattern = new BotBreakInterpretedYamlPattern(fallbackPath).
                                    configure(offsetX, offsetY, offsetZ, outerRadius, innerRadius, AxisDirection.DOWN);
            BotLogger.info(this.isLogged(),
                    "ℹ️ 📐 Используется дефолтный YAML-паттерн: " + patternName);
        }

        BotLogger.info(this.isLogged(), "📐 Выбран паттерн разрушения: " + breakPattern.getName());

        bot.setAutoPickupEnabled(shouldPickup);

        BotLogger.info(this.isLogged(),
                "⚙️ BotTaskBreakBlock настроена: " + (targetMaterials == null ? "ВСЕ БЛОКИ" : targetMaterials));
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
        BotLogger.info(this.isLogged(), "🎯 Установлены целевые блоки: " + materials);
    }

    public void setBreakPattern(IBotDestructionPattern ptrn) {
        breakPattern = ptrn;
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.info(this.isLogged(), "📜 Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void executeTask() {

        BotLogger.info(this.isLogged(), "🚀 Запуск задачи разрушения блоков для бота " + bot.getId() +
                " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");

        if (breakPattern == null) {
            if (!StringUtil.isEmpty(patternName)) {
                Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, patternName);
                this.breakPattern = new BotBreakInterpretedYamlPattern(fallbackPath).
                                        configure(offsetX, offsetY, offsetZ, outerRadius, innerRadius, breakDirection);
                BotLogger.info(this.isLogged(),
                        "ℹ️ 📐 Используется YAML-паттерн: " + patternName);
                
            } else {
                Path fallbackPath = Paths.get(BotConstants.PLUGIN_PATH_PATTERNS_BREAK, BotConstants.DEFAULT_PATTERN_BREAK);
                this.breakPattern = new BotBreakInterpretedYamlPattern(fallbackPath).configure(offsetX, offsetY, offsetZ, outerRadius, innerRadius, breakDirection);
                BotLogger.info(this.isLogged(),
                        "ℹ️ 📐 Используется дефолтный YAML-паттерн: " + BotConstants.DEFAULT_PATTERN_BREAK);
            }
        }

        if (isInventoryFull() || isEnoughBlocksCollected()) {
            BotLogger.info(this.isLogged(), "⛔ Задача завершена: инвентарь полон или ресурсов достаточно");
            isDone = true;
            bot.getRuntimeStatus().setTargetLocation(null);
            return;
        }

        bot.pickupNearbyItems(shouldPickup);

        if (getGeoMap() == null) {
            BotLogger.info(this.isLogged(), "🔍 Запускаем 3D-сканирование окружающей среды.");
            BotSonar3DTask scanTask = new BotSonar3DTask(bot, this, outerRadius, innerRadius);
            scanTask.configure(scanMode);
            bot.addTaskToQueue(scanTask);
            isDone = false;
            return;
        }

        if (breakPattern.isFinished()) {
            BotLogger.info(this.isLogged(), "🏁 Все блоки по паттерну обработаны. Завершаем задачу.");
            isDone = true;
            return;
        }

        BotCoordinate3D coordinate = breakPattern.findNextBlock(bot);

        if (coordinate == null) {
            return;
        }

        Location targetLocation = new Location(Bukkit.getWorlds().get(0), coordinate.x, coordinate.y, coordinate.z);

        bot.getRuntimeStatus().setTargetLocation(targetLocation);

        if (bot.getRuntimeStatus().getTargetLocation() != null) {

            setObjective("Probing: " + BotUtils.getBlockName(bot.getRuntimeStatus().getTargetLocation().getBlock())
                    + " at " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));

            if (isInProtectedZone(bot.getRuntimeStatus().getTargetLocation())) {
                BotLogger.info(this.isLogged(), "⛔ " + bot.getId() + " в запретной зоне, НЕ будет разрушать блок: " +
                        BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));
                isDone = true;
                bot.getRuntimeStatus().setTargetLocation(null);
                return;
            }

            if (!BotUtils.isBreakableBlock(bot.getRuntimeStatus().getTargetLocation())) {
                BotLogger.info(this.isLogged(), "⛔ Неразрушаемый блок: "
                        + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));
                bot.getRuntimeStatus().setTargetLocation(null);
                return;
            }

            Material mat = bot.getRuntimeStatus().getTargetLocation().getBlock().getType();

            if (BotUtils.requiresTool(mat)) {
                if (!BotInventory.equipRequiredTool(bot, mat)) {
                    BotLogger.info(this.isLogged(), "🙈 Не удалось взять инструмент в руку. Пропускаем.");
                    bot.getRuntimeStatus().setTargetLocation(null);
                    return;
                }
            }

            setObjective("Breaking: " + BotUtils.getBlockName(bot.getRuntimeStatus().getTargetLocation().getBlock()));

            BotUseHandTask handTask = new BotUseHandTask(bot, "🪨⛏");
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
            BotLogger.info(this.isLogged(), "🔄 " + bot.getId() + " Целевых блоков нет! Запускаем полное разрушение.");
            bot.addTaskToQueue(new BotBreakAnyTask(bot));
            isDone = false;
        } else {
            setObjective("");
            BotLogger.info(this.isLogged(), "❌ " + bot.getId() + " Нет подходящих блоков. Завершаем.");
            isDone = true;
        }
    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
        BotLogger.info(this.isLogged(), "📦 Проверка инвентаря: " + (full ? "полон" : "есть место"));
        return full;
    }

    private boolean isEnoughBlocksCollected() {
        boolean enough = BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
        BotLogger.info(this.isLogged(), "📊 Проверка количества блоков: " + (enough ? "достаточно" : "нужно больше"));
        return enough;
    }

    private boolean isInProtectedZone(Location location) {
        boolean protectedZone = BotZoneManager.getInstance().isInProtectedZone(location);
        if (protectedZone) {
            BotLogger.info(this.isLogged(), "🛑 Блок в запретной зоне, разрушение запрещено.");
        }
        return protectedZone;
    }

}
