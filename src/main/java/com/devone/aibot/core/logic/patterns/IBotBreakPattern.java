package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

public interface IBotBreakPattern {
    Location findNextBlock(Bot bot, Map<Location, ?> geoMap, Set<Material> targetMaterials);
    String getName();
}
