package com.devone.aibot.core.logic.patterns;

import org.yaml.snakeyaml.Yaml;

import com.devone.aibot.core.logic.patterns.destruction.BotBreakInterpretedPattern;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BotPatternLoader {

    public static BotBreakInterpretedPattern loadPattern(Path filePath) {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            return BotBreakInterpretedPattern.fromYaml(data);
        } catch (Exception e) {
            System.err.println("[PatternLoader] Error loading pattern from " + filePath + ": " + e.getMessage());
            return null;
        }
    }
}
