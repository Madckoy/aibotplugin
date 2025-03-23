package com.devone.aibot.core.logic.patterns.destruction;

import java.util.List;

import org.bukkit.Location;

public interface IBotDestructionGenerator {
    List<Location> generate(Location center, int radius);
    String getName();
}
