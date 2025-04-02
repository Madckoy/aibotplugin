package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.patterns.BotPatternGenerationParams;
import com.devone.aibot.utils.BotCoordinate3D;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;
import com.devone.aibot.utils.BotCoordinateComparators;
import com.devone.aibot.utils.BotLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Реализация разрушительного паттерна, загружаемого из YAML.
 * Полностью заменяет старую Java-логику.
 */
public class BotBreakInterpretedYamlPattern implements IBotDestructionPattern {

    private final Path yamlPath;

    private BotCoordinatesGenerator generator;

    private boolean initialized = false;

    private AxisDirection breakDirection = null;
    private int offsetX, offsetY, offsetZ, outerRadius, innerRadius;  

    private final Queue<BotCoordinate3D> blocksToBreak = new LinkedList<>();
    private boolean inverted = false;
    
        public BotBreakInterpretedYamlPattern(Path path) {
            this.yamlPath = path;
        }
     
        @Override    
        public IBotDestructionPattern configure(int offsetX, int offsetY, int offsetZ, int outerRadius, int innerRadius, AxisDirection breakDirection) {

            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            
            this.outerRadius = outerRadius;
            this.innerRadius = innerRadius;

            this.breakDirection = breakDirection;

    
            BotLogger.info(true, "🛠️ Начинаем загрузку YAML-паттерна: " + yamlPath);
    
            try (InputStream inputStream = Files.newInputStream(yamlPath)) {
                this.generator = BotCoordinatesGenerator.loadYmlFromStream(inputStream);
            if (this.generator != null) {
                BotLogger.info(true, "✅ Паттерн успешно загружен из YAML: " + yamlPath.getFileName());
            } else {
                BotLogger.info(true, "❌ loadFromYaml() вернул null для файла: " + yamlPath);
            }
        } catch (IOException e) {
            BotLogger.info(true, "❌ Ошибка при открытии YAML-файла: " + yamlPath + " — " + e.getMessage());
        }

        return this;
    }

    public BotCoordinate3D findNextBlock(Bot bot ) {
        if (this.generator == null) {
            BotLogger.info(true, "🚨 ❌ Паттерн не инициализирован! YAML: " + yamlPath);
            return null;
        }

        if (!initialized) {
            BotLogger.info(true, "🔁 Генерация точек по паттерну: " + yamlPath);
                                                                     
            BotPatternGenerationParams params = new BotPatternGenerationParams(bot.getRuntimeStatus().getCurrentLocation().getBlockX(), 
                                                                               bot.getRuntimeStatus().getCurrentLocation().getBlockY(), 
                                                                               bot.getRuntimeStatus().getCurrentLocation().getBlockZ(), 
                                                                               offsetX, offsetY, offsetZ, outerRadius, innerRadius);

            List<BotCoordinate3D> inner_points = generator.generateInnerPoints(params);

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
                
                BotLogger.info(true, "✅ Added " + blocksToBreak.size() + " coordinates");
            } else {
                
                BotLogger.info(true, "⚠️ Паттерн YAML не вернул ни одной точки для разрушения.");

            }

            initialized = true;
        }

        BotCoordinate3D next = blocksToBreak.poll();

        if (next != null) {
            BotLogger.info(true, "🎯 Next coordinate: " + next.x + ", " + next.y + ", " + next.z);
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
