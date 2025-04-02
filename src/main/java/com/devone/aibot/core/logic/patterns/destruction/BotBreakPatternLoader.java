package com.devone.aibot.core.logic.patterns.destruction;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BotBreakPatternLoader {

    public static BotCoordinatesGenerator loadYamlFile(Path path) {
        try (InputStream in = Files.newInputStream(path)) {

            return loadYmlFromStream(in);

        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки YAML паттерна из файла " + path + ": " + e.getMessage());
            return null;
        }
    }

    public static BotCoordinatesGenerator loadYmlFromStream(InputStream inputStream) {
        try {
    
            return BotCoordinatesGenerator.loadYmlFromStream(inputStream); // ✅ Используем встроенный разбор

        } catch (Exception e) {
            System.err.println("❌ Ошибка парсинга YAML паттерна: " + e.getMessage());
            return null;
        }
    }
    
}
