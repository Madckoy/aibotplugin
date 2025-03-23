package com.devone.aibot.core.logic.patterns.destruction;

import java.util.List;

import org.bukkit.Location;

public class YamlDestructionPatternGenerator extends AbstractDestructionGenerator {
    private final String patternFileName;

    public YamlDestructionPatternGenerator(String patternFileName) {
        this.patternFileName = patternFileName;
    }

    @Override
    public String getName() {
        return "Yaml:" + patternFileName;
    }

    @Override
    protected List<Location> doGenerate(Location center) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doGenerate'");
    }

}
