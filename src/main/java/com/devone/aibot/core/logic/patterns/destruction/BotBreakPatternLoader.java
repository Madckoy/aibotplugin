package com.devone.aibot.core.logic.patterns.destruction;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class BotBreakPatternLoader {

    public static BotBreakInterpretedPattern loadFromYaml(Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            return loadFromYaml(in);
        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки YAML паттерна из файла " + path + ": " + e.getMessage());
            return null;
        }
    }

    public static BotBreakInterpretedPattern loadFromYaml(InputStream inputStream) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> raw = yaml.load(inputStream);
    
            return BotBreakInterpretedPattern.fromYaml(raw); // ✅ Используем встроенный разбор
        } catch (Exception e) {
            System.err.println("❌ Ошибка парсинга YAML паттерна: " + e.getMessage());
            return null;
        }
    }
    
}
