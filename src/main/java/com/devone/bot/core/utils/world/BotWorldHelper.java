package com.devone.bot.core.utils.world;

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

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotWorldHelper {

    public static boolean isNight(Bot bot) {
        if (bot == null || bot.getNPCEntity() == null) return false;
        World world = getBotWorld(bot);
        long time = world.getTime();
        return time >= 13000 && time <= 23999;
    }

    public static World getBotWorld(Bot bot) {
        return bot != null && bot.getNPCEntity() != null
            ? bot.getNPCEntity().getWorld()
            : Bukkit.getWorlds().get(0);
    }

    public static BotPosition getWorldSpawnLocation() {
        Location spawnLocation = getWorld().getSpawnLocation();
;
        return BotWorldHelper.locationToBotPosition(spawnLocation);
    }

    public static World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static BotPosition locationToBotPosition(Location loc) {
        return new BotPosition((int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
    }

    public static BotBlockData blockToBotBlockData(Block block) {
        BotBlockData blockData = new BotBlockData();
        blockData.setX(block.getX());
        blockData.setY(block.getY());
        blockData.setZ(block.getZ());
        blockData.setType(block.getType().toString());
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
            case WATER, BUBBLE_COLUMN, KELP, SEAGRASS, LAVA, MAGMA_BLOCK -> true;
            default -> false;
        };
    }

    public static boolean isInDangerousLiquid(Bot bot) {
        if (bot == null || bot.getNPCEntity() == null) return false;
        Location loc = bot.getNPCEntity().getLocation();
        return isDangerousLiquid(loc.getBlock());
    }

    public static boolean isBlockInDangerousLiquid(BotPosition location) {
        Block block = botPositionToWorldBlock(location);
        return isDangerousLiquid(block);
    }

    public static boolean isNearWater(Bot bot) {
        BotPosition loc = bot.getNavigator().getPosition();
        Block origin = botPositionToWorldBlock(loc);
        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Block neighbor = origin.getRelative(dx, dy, dz);
                    if (neighbor.getType() == Material.WATER) return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnUnstableGround(Bot bot) {
        BotPosition loc = bot.getNavigator().getPosition();
        Block block = botPositionToWorldBlock(new BotPosition(loc.getX(), loc.getY() - 1, loc.getZ()));
        Material ground = block.getType();
        return ground == Material.SAND || ground == Material.GRAVEL ||
               ground == Material.POWDER_SNOW || ground == Material.SNOW;
    }

    public static Location botPositionToWorldLocation(BotPosition loc) {
        if (loc == null) return null;
        World world = getWorld();
        return new Location(world, loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5);
    }

    public static Block botPositionToWorldBlock(BotPosition loc) {
        if (loc == null) return null;
        World world = getWorld();
        return world.getBlockAt(loc.getX(), loc.getY(), loc.getZ());
    }

    public static boolean isInsideWorldBounds(BotPosition loc) {
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

    public static List<Block> getNearbyBlocks(BotPosition loc, int radius) {
        List<Block> blocks = new ArrayList<>();
        if (loc == null) return blocks;
        World world = getWorld();
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

    public static BotPosition findSafeLandingBelow(BotPosition loc, int maxDepth) {
        if (loc == null) return null;
        World world = getWorld();
        for (int y = loc.getY(); y >= Math.max(0, loc.getY() - maxDepth); y--) {
            Block block = world.getBlockAt(loc.getX(), y, loc.getZ());
            if (isSolidSurface(block)) {
                return new BotPosition(loc.getX(), y + 1, loc.getZ());
            }
        }
        return null;
    }

    public static boolean isInDangerousLiquid(BotBlockData data) {
        if (data == null) return false;
        try {
            Material type = Material.valueOf(data.getType());
            return switch (type) {
                case WATER, BUBBLE_COLUMN, KELP, SEAGRASS, LAVA, MAGMA_BLOCK -> true;
                default -> false;
            };
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isBreakableBlock(Block block) {
        if (block == null) return false;
        Material type = block.getType();
        return switch (type) {
            case AIR, CAVE_AIR, VOID_AIR, BEDROCK, BARRIER, WATER,
                 END_PORTAL, END_PORTAL_FRAME,
                 STRUCTURE_BLOCK, STRUCTURE_VOID,
                 COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK -> false;
            default -> true;
        };
    }
}
