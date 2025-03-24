package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.Location;

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
    private BotBreakInterpretedPattern pattern;

    private boolean initialized = false;
    private int radius = 0;

    private final Queue<Location> blocksToBreak = new LinkedList<>();

    public BotBreakInterpretedYamlPattern(Path path) {
        this.yamlPath = path;
    }
 
    @Override    
    public IBotDestructionPattern configure(int radius) {
        this.radius = radius;
        BotLogger.trace(true, "🛠️ Начинаем загрузку YAML-паттерна: " + yamlPath);

        try (InputStream inputStream = Files.newInputStream(yamlPath)) {
            this.pattern = BotBreakPatternLoader.loadFromYaml(inputStream);
            if (this.pattern != null) {
                BotLogger.info(true, "✅ Паттерн успешно загружен из YAML: " + yamlPath.getFileName());
            } else {
                BotLogger.error(true, "❌ loadFromYaml() вернул null для файла: " + yamlPath);
            }
        } catch (IOException e) {
            BotLogger.error(true, "❌ Ошибка при открытии YAML-файла: " + yamlPath + " — " + e.getMessage());
        }

        return this;
    }


    public Location findNextBlock(Bot bot) {
        if (this.pattern == null) {
            BotLogger.error(true, "🚨 ❌ Паттерн не инициализирован! YAML: " + yamlPath);
            return null;
        }

        if (!initialized) {
            BotLogger.trace(true, "🔁 Генерация точек по паттерну...");
            List<Location> points = pattern.generate(
                    new BotBreakInterpretedPattern.PatternContext(
                            bot.getRuntimeStatus().getCurrentLocation(),
                            radius));

            if (points != null && !points.isEmpty()) {
                blocksToBreak.addAll(points);
                BotLogger.trace(true, "✅ Добавлено " + points.size() + " точек для разрушения");
            } else {
                BotLogger.warn(true, "⚠️ Паттерн YAML не вернул ни одной точки для разрушения.");
            }

            initialized = true;
        }

        Location next = blocksToBreak.poll();
        if (next != null) {
            BotLogger.trace(true, "🎯 Следующий блок: " + next.getBlockX() + ", " + next.getBlockY() + ", " + next.getBlockZ());
        }
        return next;
    }


    public boolean isFinished() {
        return initialized && blocksToBreak.isEmpty();
    }


    public String getName() {
        return "BotBreakInterpretedYamlPattern(" + yamlPath.getFileName().toString() + ")";
    }


    public List<Location> getAllPlannedBlocks() {
        return new ArrayList<>(blocksToBreak);
    }

}
