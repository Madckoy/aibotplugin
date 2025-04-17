package com.devone.bot.core.logic.task.excavate.patterns.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.excavate.patterns.IBotExcavatePattern;
import com.devone.bot.core.logic.task.excavate.patterns.generator.params.BotExcavatePatternGenerationParams;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.blocks.BotLocationComparators;
import com.devone.bot.utils.logger.BotLogger;

/**
 * Реализация разрушительного паттерна, загружаемого из YAML.
 * Полностью заменяет старую Java-логику.
 */
public class BotExcavateInterpretedYamlPattern implements IBotExcavatePattern {

    private final Path yamlPath;

    private BotExcavateCoordinatesGenerator generator;

    private boolean initialized = false;

    private int offsetX, offsetY, offsetZ, outerRadius, innerRadius;  

    private final Queue<BotLocation> blocksToBreak = new LinkedList<>();
    
        public BotExcavateInterpretedYamlPattern(Path path) {
            this.yamlPath = path;
        }
     
        @Override    
        public IBotExcavatePattern configure(int offsetX, int offsetY, int offsetZ, int outerRadius, int innerRadius) {

            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            
            this.outerRadius = outerRadius;
            this.innerRadius = innerRadius;

    
            BotLogger.info("🛠️", true, "Начинаем загрузку YAML-паттерна: " + yamlPath);
    
            try (InputStream inputStream = Files.newInputStream(yamlPath)) {
                this.generator = BotExcavateCoordinatesGenerator.loadYmlFromStream(inputStream);
            if (this.generator != null) {
                BotLogger.info("✅", true, "Паттерн успешно загружен из YAML: " + yamlPath.getFileName());
            } else {
                BotLogger.info("❌", true, "loadFromYaml() вернул null для файла: " + yamlPath);
            }
        } catch (IOException e) {
            BotLogger.info("❌", true, "Ошибка при открытии YAML-файла: " + yamlPath + " — " + e.getMessage());
        }

        return this;
    }

    public BotLocation findNextBlock(Bot bot ) {
        if (this.generator == null) {
            BotLogger.info("🚨 ", true, "Паттерн не инициализирован! YAML: " + yamlPath);
            return null;
        }

        if (!initialized) {
            BotLogger.info("🔁 ", true, "Генерация точек по паттерну: " + yamlPath);
                                                                     
            BotExcavatePatternGenerationParams params = new BotExcavatePatternGenerationParams(bot.getBrain().getCurrentLocation().getX(), 
                                                                               bot.getBrain().getCurrentLocation().getY(), 
                                                                               bot.getBrain().getCurrentLocation().getZ(), 
                                                                               offsetX, offsetY, offsetZ, outerRadius, innerRadius);

            BotLogger.info("Params:", true, params.toString());                                                                  

            List<BotLocation> inner_points = generator.generateInnerPoints(params);
            
            //String pointsLog = inner_points.stream()
            //.map(p -> String.format("%d, %d, %d", p.getX(), p.getY(), p.getZ()))
            //.collect(Collectors.joining(", "));
        
            //BotLogger.info("🔢", true, String.format("Generated %d points: [%s]", inner_points.size(), pointsLog));        
            BotLogger.info("🔢", true, String.format("Generated %d points", inner_points.size()));     

            boolean isInverted = generator.getInverted();

            Set<BotLocation> result = new HashSet<>();

            if(isInverted) {
                List<BotLocation> all =  generator.generateOuterPoints(params);
                result.addAll(all);  // Генерируем весь куб
                result.removeAll(inner_points);  // Удаляем внутреннюю область
            } else {
                // Если не inverted, просто генерируем яму
                result.addAll(inner_points);  // Только внутренняя область
            }

            
            List<BotLocation> toBeRemoved = new ArrayList<>(result);

            // ✅ Сортировка по направлению
            Comparator<BotLocation> sortingComparator = BotLocationComparators.byAxisDirection(AxisDirection.DOWN);
            if (sortingComparator != null) {
                toBeRemoved.sort(sortingComparator);
            }
                                  
            //toBeRemoved.removeAll(kept);

            if (toBeRemoved != null && !toBeRemoved.isEmpty()) {

                blocksToBreak.addAll(toBeRemoved);
                
                BotLogger.info("✅", true, "Added " + blocksToBreak.size() + " coordinates");
            } else {
                
                BotLogger.info("⚠️", true, "Паттерн YAML не вернул ни одной точки для разрушения.");

            }

            BotLogger.info("Block:", true, blocksToBreak.toString());

            initialized = true;
        }

        BotLocation next = blocksToBreak.poll();

        if (next != null) {
            BotLogger.info("🎯", true, "Next coordinate: " + next.getX() + ", " + next.getY() + ", " + next.getZ());
        }
        return next;
    }


    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }


    public String getName() {
        return "BotBreakInterpretedYamlPattern(" + yamlPath.getFileName().toString() + ")";
    }


    public List<BotLocation> getAllPlannedBlocks() {
        return new ArrayList<>(blocksToBreak);
    }

}
