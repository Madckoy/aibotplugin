package com.devone.bot.core.logic.task.excavate.patterns.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.excavate.patterns.IBotExcavatePattern;
import com.devone.bot.core.logic.task.excavate.patterns.generator.params.BotExcavatePatternGenerationParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotCoordinateComparators;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;
import com.devone.bot.utils.logger.BotLogger;

/**
 * Реализация разрушительного паттерна, загружаемого из YAML.
 * Полностью заменяет старую Java-логику.
 */
public class BotExcavateInterpretedYamlPattern implements IBotExcavatePattern {

    private final Path yamlPath;

    private BotExcavateCoordinatesGenerator generator;

    private boolean initialized = false;

    private AxisDirection breakDirection = null;
    private int offsetX, offsetY, offsetZ, outerRadius, innerRadius;  

    private final Queue<BotCoordinate3D> blocksToBreak = new LinkedList<>();
    
        public BotExcavateInterpretedYamlPattern(Path path) {
            this.yamlPath = path;
        }
     
        @Override    
        public IBotExcavatePattern configure(int offsetX, int offsetY, int offsetZ, int outerRadius, int innerRadius, AxisDirection breakDirection) {

            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            
            this.outerRadius = outerRadius;
            this.innerRadius = innerRadius;

            this.breakDirection = breakDirection;

    
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

    public BotCoordinate3D findNextBlock(Bot bot ) {
        if (this.generator == null) {
            BotLogger.info("🚨 ", true, "Паттерн не инициализирован! YAML: " + yamlPath);
            return null;
        }

        if (!initialized) {
            BotLogger.info("🔁 ", true, "Генерация точек по паттерну: " + yamlPath);
                                                                     
            BotExcavatePatternGenerationParams params = new BotExcavatePatternGenerationParams(bot.getRuntimeStatus().getCurrentLocation().x, 
                                                                               bot.getRuntimeStatus().getCurrentLocation().y, 
                                                                               bot.getRuntimeStatus().getCurrentLocation().z, 
                                                                               offsetX, offsetY, offsetZ, outerRadius, innerRadius);

            BotLogger.info("Params:", true, params.toString());                                                                  

            List<BotCoordinate3D> inner_points = generator.generateInnerPoints(params);
            
            String pointsLog = inner_points.stream()
            .map(p -> String.format("(%d, %d, %d)", p.x, p.y, p.z))
            .collect(Collectors.joining(", "));
        
            BotLogger.info("🔢", true, String.format("Generated %d points: [%s]", inner_points.size(), pointsLog));        

            boolean isInverted = generator.getInverted();

            Set<BotCoordinate3D> result = new HashSet<>();

            if(isInverted) {
                List<BotCoordinate3D> all =  generator.generateOuterPoints(params);
                result.addAll(all);  // Генерируем весь куб
                result.removeAll(inner_points);  // Удаляем внутреннюю область
            } else {
                // Если не inverted, просто генерируем яму
                result.addAll(inner_points);  // Только внутренняя область
            }

            
            List<BotCoordinate3D> toBeRemoved = new ArrayList<>(result);

            // ✅ Сортировка по направлению
            Comparator<BotCoordinate3D> sortingComparator = BotCoordinateComparators.byAxisDirection(breakDirection);
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

        BotCoordinate3D next = blocksToBreak.poll();

        if (next != null) {
            BotLogger.info("🎯", true, "Next coordinate: " + next.x + ", " + next.y + ", " + next.z);
        }
        return next;
    }


    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }


    public String getName() {
        return "BotBreakInterpretedYamlPattern(" + yamlPath.getFileName().toString() + ")";
    }


    public List<BotCoordinate3D> getAllPlannedBlocks() {
        return new ArrayList<>(blocksToBreak);
    }

}
