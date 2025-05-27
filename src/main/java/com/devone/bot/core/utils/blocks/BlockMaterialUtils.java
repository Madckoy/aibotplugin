package com.devone.bot.core.utils.blocks;

import java.util.Set;

public class BlockMaterialUtils {

    // --- Базовые типы блоков по поведению ---
    public static final Set<String> AIR_TYPES = Set.of("AIR", "CAVE_AIR", "VOID_AIR");

    public static final Set<String> DANGEROUS_PASSABLE = Set.of(
        "WATER", "POWDER_SNOW", "MAGMA_BLOCK", "CAMPFIRE", "SOUL_CAMPFIRE",
        "LAVA", "FIRE", "NETHER_PORTAL", "END_PORTAL"
    );

    public static final Set<String> SAFE_IMPASSABLE = Set.of(   
        "DIRT", "STONE", "GRASS_BLOCK", "COBBLESTONE", "WOOD", "OAK_PLANKS",
        "SPRUCE_PLANKS", "BIRCH_PLANKS", "JUNGLE_PLANKS", "ACACIA_PLANKS", "DARK_OAK_PLANKS",
        "MANGROVE_PLANKS", "CHERRY_PLANKS", "BAMBOO_BLOCK",
        "BRICKS", "SANDSTONE", "NETHERRACK", "END_STONE", "DEEPSLATE",
        "OBSIDIAN", "GLASS", "ICE", "PACKED_ICE", "BLUE_ICE",
        "SNOW_BLOCK", "CLAY", "TERRACOTTA", "BLACKSTONE", "TUFF",
        "BASALT", "PURPUR_BLOCK", "QUARTZ_BLOCK", "CONCRETE", "CONCRETE_POWDER",
        "SHROOMLIGHT", "HONEY_BLOCK", "SLIME_BLOCK"
    );

    public static final Set<String> DANGEROUS_IMPASSABLE = Set.of(
        "CACTUS", "WITHER_ROSE", "SWEET_BERRY_BUSH",
        "POINTED_DRIPSTONE", "DRIPSTONE_BLOCK",
        "MAGMA_BLOCK", "CAMPFIRE", "SOUL_CAMPFIRE", "LAVA", "FIRE",
        "SOUL_FIRE", "BEE_NEST", "BEEHIVE", "LIGHTNING_ROD", "ANVIL",
        "CHIPPED_ANVIL", "DAMAGED_ANVIL", "END_CRYSTAL", "RESPAWN_ANCHOR",
        "SCULK_SHRIEKER", "SCULK_SENSOR", "POWDER_SNOW", "UNKNOWN"
    );

    public static final Set<String> COVER_TYPES = Set.of(
        "SHORT_GRASS", "ICE", "SNOW", "CARPET", "TALL_GRASS", "GRASS", "FERN",
        "LARGE_FERN", "DEAD_BUSH", "SEAGRASS", "TALL_SEAGRASS", "FLOWER", "DANDELION",
        "POPPY", "BLUE_ORCHID", "ALLIUM", "AZURE_BLUET", "RED_TULIP", "ORANGE_TULIP",
        "WHITE_TULIP", "PINK_TULIP", "OXEYE_DAISY", "CORNFLOWER", "LILY_OF_THE_VALLEY",
        "SUNFLOWER", "ROSE_BUSH", "PEONY", "LILAC", "MOSS_CARPET", "ROOTS",
        "HANGING_ROOTS", "SUGAR_CANE", "VINE"
    );

    public static final Set<String> NAVIGATION_OBSTACLES = Set.of(
        "COCOA", "LEVER", "LANTERN", "TORCH", "WALL_TORCH", "ITEM_FRAME", "FLOWER_POT",
        "SWEET_BERRY_BUSH", "CAVE_VINES", "POINTED_DRIPSTONE", "AZALEA", "POTTED_AZALEA_BUSH",
        "BUTTON", "TRIPWIRE_HOOK", "BAMBOO_SAPLING"
    );

    // --- Определения поведения ---
    public static boolean isClimbable(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();
        return type.contains("VINE") || type.contains("LADDER") || type.contains("SCAFFOLDING");
    }

    public static boolean isPassableForMovement(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();
        return AIR_TYPES.contains(type)
            || COVER_TYPES.contains(type)
            || DANGEROUS_PASSABLE.contains(type);
    }


    public static boolean canBotStandInside(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();

        if (AIR_TYPES.contains(type)) return true;
        if (COVER_TYPES.contains(type)) return true;
        if (type.equals("WATER") || type.equals("POWDER_SNOW")) return true;

        return false;
    }

    public static boolean isSolidEnoughToStandOn(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();

        if (AIR_TYPES.contains(type)) return false;
        if (COVER_TYPES.contains(type)) return false;
        if (type.equals("WATER") || type.equals("LAVA")) return false;

        if (type.contains("FENCE") || type.contains("WALL") || type.contains("DOOR")
            || type.contains("TRAPDOOR") || type.contains("BAMBOO") || type.contains("BARREL")) {
            return false;
        }

        return true;
    }

    public static boolean isCover(BotBlockData block) {
        return block != null && COVER_TYPES.contains(block.getType().toUpperCase());
    }

    public static boolean isAir(BotBlockData block) {
        return block != null && AIR_TYPES.contains(block.getType().toUpperCase());
    }

    public static boolean isDangerous(BotBlockData block) {
        if (block == null) return true;
        String type = block.getType().toUpperCase();
        return DANGEROUS_PASSABLE.contains(type) || DANGEROUS_IMPASSABLE.contains(type);
    }

    public static boolean isPassableDangerous(BotBlockData block) {
        return block != null && DANGEROUS_PASSABLE.contains(block.getType().toUpperCase());
    }

    public static boolean isPassableAbove(BotBlockData block) {
        return canBotStandInside(block);
    }

    public static boolean isLeaves(BotBlockData block) {
        return block != null && block.getType().toUpperCase().contains("LEAVES");
    }

    // --- Новые методы для soft-обструкций ---

    public static boolean isNavigationObstacle(BotBlockData block) {
        if (block == null || block.getType() == null) return false;
        return NAVIGATION_OBSTACLES.contains(block.getType().toUpperCase());
    }

    public static boolean isSafeImpassable(BotBlockData block) {
        return block != null && SAFE_IMPASSABLE.contains(block.getType().toUpperCase());
    }

    public static boolean isDangerousImpassable(BotBlockData block) {
        return block != null && DANGEROUS_IMPASSABLE.contains(block.getType().toUpperCase());
    }
}
