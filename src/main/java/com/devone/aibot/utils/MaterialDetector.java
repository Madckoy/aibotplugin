package com.devone.aibot.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

public class MaterialDetector {
    
    private final int radiusXZ;
    private final int radiusY;

    public static final Set<Material> LEAF_MATERIALS = EnumSet.of(
        Material.OAK_LEAVES, Material.DARK_OAK_LEAVES, Material.BIRCH_LEAVES,
        Material.ACACIA_LEAVES, Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES,
        Material.CHERRY_LEAVES, Material.SPRUCE_LEAVES
    );

    
    public MaterialDetector(int radius) {
        this.radiusXZ = radius;
        this.radiusY = radius;
    }

    public MaterialDetector(int radiusXZ, int radiusY) {
        this.radiusXZ = radiusXZ;
        this.radiusY = radiusY;
    }

    public Location findClosestMaterial(Material mat, Location start) {
        return findClosestMaterialInSet(Set.of(mat), start);
    }

    public Location findClosestMaterialInSet(Set<Material> materials, Location start) {
        Location bestLocation = null;
        double minDistance = Double.MAX_VALUE;
    
        for (int y = 0; y <= radiusY; y++) {
            for (int x = -radiusXZ; x <= radiusXZ; x++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    Location loc = start.clone().add(x, y, z);
                    
                    if (isValidMaterial(materials, loc)) {
                        double distance = start.distanceSquared(loc);
                        
                        if (distance < minDistance) {
                            minDistance = distance;
                            bestLocation = loc;
                            
                            // 🚀 Оптимизация: если нашли блок в 1 блоке от бота, останавливаем поиск!
                            if (distance == 1) {
                                return bestLocation;
                            }
                        }
                    }
                }
            }
            
            if (y > 0) {
                for (int x = -radiusXZ; x <= radiusXZ; x++) {
                    for (int z = -radiusXZ; z <= radiusXZ; z++) {
                        Location loc = start.clone().add(x, -y, z);
                        
                        if (isValidMaterial(materials, loc)) {
                            double distance = start.distanceSquared(loc);
                            
                            if (distance < minDistance) {
                                minDistance = distance;
                                bestLocation = loc;
                                
                                if (distance == 1) {
                                    return bestLocation;
                                }
                            }
                        }
                    }
                }
            }
        }
    
        // **Логируем найденный блок**
        if (bestLocation != null) {
            BotLogger.debug("🔍 Ближайший найденный блок: " + bestLocation.getBlock().getType() +
                " на " + BotUtils.formatLocation(bestLocation));
        } else {
            BotLogger.warn("⚠️ Блоков не найдено в радиусе " + radiusXZ);
        }
    
        return bestLocation;
    }
    private boolean isValidMaterial(Set<Material> materials, Location loc) {
        Material type = loc.getBlock().getType();
    
        if (type == Material.AIR || type == Material.WATER || type == Material.CAVE_AIR) {
            return false; // ❌ Игнорируем воздух, воду и пещерный воздух!
        }
    
        return materials == null || materials.contains(type);
    }

    public Location findClosestSolidBlock(Location currentLocation) {
        for (int y = currentLocation.getBlockY(); y <= currentLocation.getBlockY() + 1; y++) {
            Location checkLoc = new Location(currentLocation.getWorld(), currentLocation.getBlockX(), y, currentLocation.getBlockZ());
            Material type = checkLoc.getBlock().getType();
    
            if (type.isSolid()) { // ✅ Проверяем, не воздух ли это
                return checkLoc;
            }
        }
        return null;
    }
    
}
