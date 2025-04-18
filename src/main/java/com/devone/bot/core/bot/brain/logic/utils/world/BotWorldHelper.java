
package com.devone.bot.core.bot.brain.logic.utils.world;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;

public class BotWorldHelper {

    public static boolean isNight(Bot bot) {
        if (bot == null || bot.getNPCEntity() == null) return false;
        World world = getWorld();
        long time = world.getTime();
        return time >= 13000 && time <= 23999;
    }

    public static World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static Block getBlockAt_old(BotLocation location) {
        World world = getWorld();
        if (world == null) return null;
        return world.getBlockAt(location.getX(), location.getY(), location.getZ());
    }

    public static Location getWorldLocation(BotLocation coordinate) {
        return getBlockAt(coordinate).getLocation();
    }

    public static BotBlockData getWorldSpawnLocation() {
        Location spawnLocation = getWorld().getSpawnLocation();
        BotBlockData blockData = new BotBlockData();
        blockData.setX(spawnLocation.getBlockX());
        blockData.setY(spawnLocation.getBlockY());
        blockData.setZ(spawnLocation.getBlockZ());
        return blockData;
    }

    public static BotLocation worldLocationToBotLocation(Location loc) {
        BotLocation location = new BotLocation((int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
        return location;
    }
    public static BotBlockData worldBlockToBotBlock(Block block) {
        BotBlockData blockData = new BotBlockData();
        blockData.setX(block.getX());
        blockData.setY(block.getY());
        blockData.setZ(block.getZ());
        blockData.setType(block.getType().toString());
        blockData.setBot(false);
        return blockData;
    }

    public static LivingEntity findLivingEntityByUUID(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity && entity.getUniqueId().equals(uuid)) {
                    return (LivingEntity) entity;
                }
            }
        }
        return null;
    }

    public static boolean isDangerousLiquid(Block block) {
        if (block == null) return false;
        return switch (block.getType()) {
            case WATER, BUBBLE_COLUMN, KELP, SEAGRASS,
                 LAVA, MAGMA_BLOCK -> true;
            default -> false;
        };
    }

    public static boolean isInDangerousLiquid(Bot bot) {
        if (bot == null || bot.getNPCEntity() == null) return false;
        Location loc = bot.getNPCEntity().getLocation();
        return isDangerousLiquid(loc.getBlock());
    }

    public static boolean isBlockInDangerousLiquid(BotLocation location) {
        Block block = getBlockAt(location);
        return isDangerousLiquid(block);
    }

    public static boolean isNearWater(Bot bot) {
        BotLocation loc = bot.getNavigation().getLocation();
        Block block = getBlockAt(loc);
        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Material material = block.getType();
                    if (material == Material.WATER) return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnUnstableGround(Bot bot) {
        BotLocation loc = bot.getNavigation().getLocation();
        Block block = getBlockAt(new BotLocation(loc.getX(), loc.getY() - 1, loc.getZ()));
        Material ground = block.getType();
        return ground == Material.SAND || ground == Material.GRAVEL ||
               ground == Material.POWDER_SNOW || ground == Material.SNOW;
    }

    public static Block getBlockAt(BotLocation loc) {
        if (loc == null) return null;
        World world = Bukkit.getWorlds().get(0);
        return world.getBlockAt(loc.getX(), loc.getY(), loc.getZ());
    }

    public static boolean isInsideWorldBounds(BotLocation loc) {
        return loc != null && loc.getY() >= 0 && loc.getY() <= 255;
    }

    public static boolean isLiquid(Block block) {
        if (block == null) return false;
        Material type = block.getType();
        return type == Material.WATER || type == Material.LAVA;
    }

    public static boolean isSolidSurface(Block block) {
        return block != null && block.getType().isSolid();
    }

    public static List<Block> getNearbyBlocks(BotLocation loc, int radius) {
        List<Block> blocks = new ArrayList<>();
        if (loc == null) return blocks;
        World world = Bukkit.getWorlds().get(0);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Block block = world.getBlockAt(loc.getX() + dx, loc.getY() + dy, loc.getZ() + dz);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static BotLocation findSafeLandingBelow(BotLocation loc, int maxDepth) {
        if (loc == null) return null;
        World world = Bukkit.getWorlds().get(0);
        for (int y = loc.getY(); y >= Math.max(0, loc.getY() - maxDepth); y--) {
            Block block = world.getBlockAt(loc.getX(), y, loc.getZ());
            if (isSolidSurface(block)) {
                return new BotLocation(loc.getX(), y + 1, loc.getZ());
            }
        }
        return null;
    }

    public static boolean isInDangerousLiquid(BotBlockData data) {
        if (data == null) return false;
        Material type;
        try {
            type = Material.valueOf(data.getType());
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    
        return switch (type) {
            case WATER, BUBBLE_COLUMN, KELP, SEAGRASS,
                 LAVA, MAGMA_BLOCK -> true;
            default -> false;
        };
    }

    
    public static boolean isBreakableBlock(Block block) {
        if (block == null ) return false;
    
        Material type = block.getType();
    
        return switch (type) {
            case AIR, CAVE_AIR, VOID_AIR,
                 BEDROCK, BARRIER, WATER, 
                 END_PORTAL, END_PORTAL_FRAME,
                 STRUCTURE_BLOCK, STRUCTURE_VOID,
                 COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK -> false;
            default -> true;
        };
    }
    

}
