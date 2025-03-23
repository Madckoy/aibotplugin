package com.devone.aibot.core.logic.patterns.destruction;

import java.util.List;

import org.bukkit.Location;

public abstract class AbstractDestructionGenerator implements IBotDestructionGenerator {
    protected int radius;
    
    @Override
    public List<Location> generate(Location center, int radius) {
        this.radius = radius;
        return doGenerate(center);
    }

    protected abstract List<Location> doGenerate(Location center);
}
