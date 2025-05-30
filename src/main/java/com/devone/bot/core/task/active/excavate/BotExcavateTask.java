package com.devone.bot.core.task.active.excavate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
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
import com.devone.bot.core.utils.pattern.params.BotPatternRunnerParams;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.utils.zone.BotZoneManager;

public class BotExcavateTask extends BotTaskAutoParams<BotExcavateTaskParams> {

    private int maxBlocks;
    private Set<Material> targetMaterials = null;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private BotPosition basePosition;

    private BotPatternRunner runner = null;

    private boolean validated = false;

    private List<BotPosition> validatedList = new ArrayList<>();
    private Queue<BotPosition> queuedList = new LinkedList<>();

    private boolean ignoreDanger = false;
    private boolean needToRestartRunner = false;


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
        }

        BotLogger.debug(icon, isLogged(),
                bot.getId() + " 📐 Установлен паттерн разрушения через setParams(): " + patternName);

        return this;
    }

    public String getPatternName() {
        return this.patternName;
    }

    public void setTargetMaterials(Set<Material> materials) {
        this.targetMaterials = materials;
        BotLogger.debug(icon, isLogged(), bot.getId() + " 🎯 Установлены целевые блоки: " + materials);
    }

    public Set<Material> getTargetMaterials() {
        BotLogger.debug(icon, isLogged(), bot.getId() + " 📜 Получены целевые блоки: " + targetMaterials);
        return this.targetMaterials;
    }

    @Override
    public void execute() {

        BotLogger.debug(icon, isLogged(), bot.getId() + " 🚀 Запуск задачи разрушения блоков для бота " + bot.getId() +
                " (Целевые блоки: " + (targetMaterials == null ? "ВСЕ" : targetMaterials) + ")");

        basePosition = new BotPosition(bot.getNavigator().getPosition());

        if(ignoreDanger==false) {
            // 🚨 Проверка на опасную жидкость
            if (BotWorldHelper.isInDanger(bot)) {
                BotLogger.debug(icon, isLogged(), bot.getId() + " 💧 Оказался в опасной жидкости. Переключаем паттерн на спасательный.");
                ignoreDanger = true;
                this.patternName = "escape.json";            
                needToRestartRunner = true;
            }
        }            

        if (runner == null || needToRestartRunner==true) {
            BotPatternRunnerParams params = new BotPatternRunnerParams();
            params.setFilename(this.patternName);
            runner = new BotPatternRunner();
            runner.setParams(params);
        }

        BotPosition blockPosition = null;

        setIcon("📜");

        setObjective(params.getObjective() + ": Processing: " + patternName);

        if (params.isPickup()) {
            bot.pickupNearbyItems();
            if (isInventoryFull() || isEnoughBlocksCollected()) {
                BotLogger.debug(icon, isLogged(),
                        bot.getId() + " ⛔ Задача завершена: инвентарь полон или ресурсов достаточно");
                this.stop();
                return;
            }
        }

        if (!runner.isLoaded()) {
            try {
                runner.load(basePosition);
                //
                validatedList = new ArrayList<>();
                queuedList.clear();
                //
                // read points, get blocks at relative position, chack if block can be broken,
                // add it to the separate list.
                // once all points processed add them to the queue, set flag "preprocessed"=true
                // and run the next cycle
                List<BotPosition> points = runner.getAllVoid();
                for (int i = 0; i < points.size(); i++) {
                    BotPosition pos = points.get(i);
                    Block block = BotWorldHelper.botPositionToWorldBlock(pos);

                    if (block.getType().toString().equals(Material.AIR.toString()) ||
                            block.getType().toString().equals(Material.CAVE_AIR.toString()) ||
                            block.getType().toString().equals(Material.VOID_AIR.toString()) ||
                            block.getType().toString().equals(Material.WATER.toString()) ||
                            block.getType().toString().equals(Material.LAVA.toString())) {

                        BotLogger.debug(icon, isLogged(), bot.getId() + " Блок не разрушимый или уже разрушен: "
                                + pos.toCompactString() + " " + block.getType());


                        continue;

                    } else {

                        if (isInProtectedZone(BotWorldHelper.locationToBotPosition(block.getLocation()))) {
                            BotLogger.debug(icon, isLogged(),
                                    bot.getId() + " ⛔ в запретной зоне, НЕ будет разрушать блок: "
                                    + block.getType());
                            continue;        
                        }

                        validatedList.add(pos);
                    }

                    String info = " ("+validatedList.size() + ")";
                    setObjective(params.getObjective() + ": Processing: " + patternName + info);
                }

                if (validatedList.isEmpty()) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ Нет доступных блоков в паттерне для разрушения.");
                    validated = false;
                } else {
                    validated=true;
                    queuedList.addAll(validatedList);   
                }

            } catch (Exception ex) {
                BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ Ошибка загрузки паттерна: " + ex.getMessage());
                stop();
                return; // exit and go to another cycle
            }
        }

        if (!validated) {
            if (blockPosition == null) {
                BotLogger.debug(icon, isLogged(), " 🏁 Нет блоков на обработку. Завершаем задачу.");
                stop();
                return;
            }
        } else {

            setIcon("🧊");           

            blockPosition = queuedList.poll();

            if (blockPosition == null) {
                BotLogger.debug(icon, isLogged(), " 🏁 Все блоки обработаны. Завершаем задачу.");
                stop();
                return;
            } else {
                BotLogger.debug(icon, isLogged(), bot.getId() + " 👆 Берем Next блок: " + blockPosition);
                Block targetBlock = BotWorldHelper.botPositionToWorldBlock(blockPosition);
            
                //bot.getNavigator().setTarget(BotWorldHelper.blockToBotBlockData(targetBlock));
            
                               
                setObjective(params.getObjective() + " " + BotUtils.getBlockName(targetBlock) + " at " + blockPosition.toCompactString());

                Material mat = targetBlock.getType();
                if (BotUtils.requiresTool(mat)) {
                    if (!BotInventory.equipRequiredTool(bot, mat)) {
                        BotLogger.debug(icon, isLogged(),
                                bot.getId() + " ❌ Не удалось взять инструмент в руку. Пропускаем.");
                        bot.getNavigator().setTarget(null);
                        return;
                    }
                }

                this.setPause(true);

                BotBlockData blockData = BotWorldHelper.blockToBotBlockData(targetBlock);
                BotHandExcavateTask handTask = new BotHandExcavateTask(bot);
                handTask.setTarget(blockData);
                BotTaskManager.push(bot, handTask);
            }
        }
    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, targetMaterials);
        BotLogger.debug(icon, isLogged(), bot.getId() + " 📦 Проверка инвентаря: " + (full ? "полон" : "есть место"));
        return full;
    }

    private boolean isEnoughBlocksCollected() {
        boolean enough = BotInventory.hasEnoughBlocks(bot, targetMaterials, maxBlocks);
        BotLogger.debug(icon, isLogged(),
                bot.getId() + " 📊 Проверка количества блоков: " + (enough ? "достаточно" : "нужно больше"));
        return enough;
    }

    private boolean isInProtectedZone(BotPosition location) {
        boolean protectedZone = BotZoneManager.getInstance().isInProtectedZone(location);
        if (protectedZone) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " 🛑 Блок в запретной зоне, разрушение запрещено.");
        }
        return protectedZone;
    }

    @Override
    public void stop() {
        this.runner = null;
        bot.getNavigator().setTarget(null);
        BotLogger.debug(icon, isLogged(), bot.getId() + " 🛑 Задача разрушения остановлена.");
        bot.getNavigator().setSuggestion(null);
        super.stop();
    }

}