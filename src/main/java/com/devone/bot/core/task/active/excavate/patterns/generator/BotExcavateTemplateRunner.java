package com.devone.bot.core.task.active.excavate.patterns.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.excavate.patterns.IBotExcavatePatternRunner;
import com.devone.bot.core.task.active.excavate.patterns.generator.params.BotExcavateTemplateRunnerParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionComparators;
import com.devone.bot.core.utils.blocks.BotAxisDirection.AxisDirection;
import com.devone.bot.core.utils.logger.BotLogger;

/**
 * Реализация разрушительного паттерна, загружаемого из YAML.
 * Полностью заменяет старую Java-логику.
 */
public class BotExcavateTemplateRunner implements IBotExcavatePatternRunner {

    private final Path yamlPath;

    private BotExcavateCoordinatesGenerator generator;

    private boolean initialized = false;
    
    private final Queue<BotPosition> blocksToBreak = new LinkedList<>();

    private BotExcavateTemplateRunnerParams params;

    public BotExcavateTemplateRunner(Path path) {

        this.yamlPath = path;
    }

    public BotExcavateTemplateRunner init(int observerX, int observerY, int observerZ) {

        BotLogger.debug("🛠️", true, "Начинаем загрузку YAML-паттерна: " + yamlPath);
    
        try (InputStream inputStream = Files.newInputStream(yamlPath)) {

            this.generator = BotExcavateCoordinatesGenerator.loadYmlFromStream(inputStream);

            if (this.generator != null) {

                BotLogger.debug("📐", true, " ✅ Паттерн успешно загружен из YAML: " + yamlPath.getFileName());

            } else {

                BotLogger.debug("📐", true, " ❌ loadFromYaml() вернул null для файла: " + yamlPath);

            }

        } catch (IOException e) {

            BotLogger.debug("📐", true, " ❌ Ошибка при открытии YAML-файла: " + yamlPath + " — " + e.getMessage());

        }

        BotExcavatePatternAttributes attributes = this.generator.getAttributes();

        params = new BotExcavateTemplateRunnerParams(observerX, 
                                                     observerY, 
                                                     observerZ, 
                                                     attributes.getOffsetOuterX(), 
                                                     attributes.getOffsetOuterY(),
                                                     attributes.getOffsetOuterZ(),
                                                     attributes.getOuterRadius(),
                                                     attributes.getOffsetInnerX(),
                                                     attributes.getOffsetInnerY(),
                                                     attributes.getOffsetInnerZ(),
                                                     attributes.getInnerRadius(),
                                                     attributes.isInverted());

        


        return this;
    } 

    @Override    
    public IBotExcavatePatternRunner setParams(BotExcavateTemplateRunnerParams params) {

        if(params==null) return this; 
    
        this.params = params;

        return this;
    }

    public BotPosition getNextBlock(Bot bot ) {
        if (this.generator == null) {
            BotLogger.debug("📐", true, " 🚨 Паттерн не инициализирован! YAML: " + yamlPath);
            return null;
        }

        if (!initialized) {
            BotLogger.debug("📐", true, " 🔁 Генерация точек по паттерну: " + yamlPath);
            
            if(params!=null) { 
                BotLogger.debug("Params:", true, params.toString());
            }        

            boolean isInverted = generator.getInverted();

            List<BotPosition> outerPts =  generator.generateOuterPoints(params);
            List<BotPosition> innerPts = generator.generateInnerPoints(params);
            List<BotPosition> removedPts = new ArrayList<>(outerPts);
            
            if(isInverted) {
                removedPts.removeAll(innerPts);
            }

            // ✅ Сортировка по направлению
            Comparator<BotPosition> sortingComparator = BotPositionComparators.byAxisDirection(AxisDirection.DOWN);
            if (sortingComparator != null) {
                removedPts.sort(sortingComparator);
            }

            if (removedPts != null && !removedPts.isEmpty()) {

                blocksToBreak.addAll(removedPts);
                
                BotLogger.debug("📐", true, " ✅ Added " + blocksToBreak.size() + " coordinates");
                BotLogger.debug("📐", true, " ✅ Added " + blocksToBreak);

            } else {
                
                BotLogger.debug("📐", true, " ⚠️ Паттерн YAML не вернул ни одной точки для разрушения.");

            }

            BotLogger.debug("Block:", true, blocksToBreak.toString());

            initialized = true;
        }

        BotPosition next = blocksToBreak.poll();

        if (next != null) {
            BotLogger.debug("📐", true, " 🎯 Next coordinate: " + next.getX() + ", " + next.getY() + ", " + next.getZ());
        }
        return next;
    }


    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }


    public String getName() {
        return "BotBreakInterpretedYamlPattern(" + yamlPath.getFileName().toString() + ")";
    }


    public List<BotPosition> getAllPlannedBlocks() {
        return new ArrayList<>(blocksToBreak);
    }

}
