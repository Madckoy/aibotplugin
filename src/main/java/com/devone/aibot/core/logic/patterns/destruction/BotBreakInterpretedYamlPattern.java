package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.patterns.BotCoordinatesGenerator;
import com.devone.aibot.utils.Bot3DCoordinate;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;
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
    private int radius = 0;

    private AxisDirection direction = null;

    private final Queue<Bot3DCoordinate> blocksToBreak = new LinkedList<>();
    
        public BotBreakInterpretedYamlPattern(Path path) {
            this.yamlPath = path;
        }
     
        @Override    
        public IBotDestructionPattern configure(int radius, AxisDirection direction) {
            this.radius = radius;
            this.direction = direction;
    
            BotLogger.trace(true, "🛠️ Начинаем загрузку YAML-паттерна: " + yamlPath);
    
            try (InputStream inputStream = Files.newInputStream(yamlPath)) {
                this.generator = BotCoordinatesGenerator.loadYmlFromStream(inputStream);
            if (this.generator != null) {
                BotLogger.info(true, "✅ Паттерн успешно загружен из YAML: " + yamlPath.getFileName());
            } else {
                BotLogger.error(true, "❌ loadFromYaml() вернул null для файла: " + yamlPath);
            }
        } catch (IOException e) {
            BotLogger.error(true, "❌ Ошибка при открытии YAML-файла: " + yamlPath + " — " + e.getMessage());
        }

        return this;
    }


    public Bot3DCoordinate findNextBlock(Bot bot ) {
        if (this.generator == null) {
            BotLogger.error(true, "🚨 ❌ Паттерн не инициализирован! YAML: " + yamlPath);
            return null;
        }

        if (!initialized) {
            BotLogger.trace(true, "🔁 Генерация точек по паттерну...");
            
            Bot3DCoordinate center = new Bot3DCoordinate(bot.getRuntimeStatus().getCurrentLocation().getBlockX(), 
                                                                   bot.getRuntimeStatus().getCurrentLocation().getBlockY(), 
                                                                   bot.getRuntimeStatus().getCurrentLocation().getBlockZ()); 
        
            List<Bot3DCoordinate> kept = generator.generateInnerPointsFromObserver(center.x, center.y, center.z, radius, direction, radius, null);
            List<Bot3DCoordinate> all =  generator.generateOuterPointsFromObserver(center.x, center.y, center.z, radius, direction, null);


            List<Bot3DCoordinate> toBeRemoved = new ArrayList<>(all);
                                  
            toBeRemoved.removeAll(kept);

            if (toBeRemoved != null && !toBeRemoved.isEmpty()) {
                blocksToBreak.addAll(toBeRemoved);
                BotLogger.trace(true, "✅ Added " + blocksToBreak.size() + " coordinates");
            } else {
                BotLogger.warn(true, "⚠️ Паттерн YAML не вернул ни одной точки для разрушения.");
            }

            initialized = true;
        }

        Bot3DCoordinate next = blocksToBreak.poll();

        if (next != null) {
            BotLogger.trace(true, "🎯 Next coordinate: " + next.x + ", " + next.y + ", " + next.z);
        }
        return next;
    }


    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }


    public String getName() {
        return "BotBreakInterpretedYamlPattern(" + yamlPath.getFileName().toString() + ")";
    }


    public List<Bot3DCoordinate> getAllPlannedBlocks() {
        return new ArrayList<>(blocksToBreak);
    }

}
