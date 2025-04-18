package com.devone.bot.core.bot.brain.logic.task.excavate.patterns.generator;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BotExcavatePatternLoader {

    public static BotExcavateCoordinatesGenerator loadYamlFile(Path path) {
        try (InputStream in = Files.newInputStream(path)) {

            return loadYmlFromStream(in);

        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки YAML паттерна из файла " + path + ": " + e.getMessage());
            return null;
        }
    }

    public static BotExcavateCoordinatesGenerator loadYmlFromStream(InputStream inputStream) {
        try {
    
            return BotExcavateCoordinatesGenerator.loadYmlFromStream(inputStream); // ✅ Используем встроенный разбор

        } catch (Exception e) {
            System.err.println("❌ Ошибка парсинга YAML паттерна: " + e.getMessage());
            return null;
        }
    }
    
}
