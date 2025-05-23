package com.devone.bot.core.utils.blocks;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BlockMaterialUtils {

    public static final Set<String> AIR_TYPES = Set.of("AIR", "CAVE_AIR", "VOID_AIR");

    public static final Set<String> DANGEROUS_PASSABLE = Set.of(
        "WATER", "POWDER_SNOW", "MAGMA_BLOCK", "CAMPFIRE", "SOUL_CAMPFIRE",
        "LAVA", "FIRE", "NETHER_PORTAL", "END_PORTAL"
    );

    public static final Set<String> DANGEROUS_IMPASSABLE = Set.of(
        "CACTUS", "WITHER_ROSE", "SWEET_BERRY_BUSH",
        "DRIPSTONE_BLOCK", "POINTED_DRIPSTONE", "UNKNOWN"
    );

    public static final Set<String> COVER_TYPES = Set.of(
        "SHORT_GRASS", "ICE", "SNOW", "CARPET", "TALL_GRASS", "GRASS", "FERN",
        "LARGE_FERN", "DEAD_BUSH", "SEAGRASS", "TALL_SEAGRASS", "FLOWER", "DANDELION",
        "POPPY", "BLUE_ORCHID", "ALLIUM", "AZURE_BLUET", "RED_TULIP", "ORANGE_TULIP",
        "WHITE_TULIP", "PINK_TULIP", "OXEYE_DAISY", "CORNFLOWER", "LILY_OF_THE_VALLEY",
        "SUNFLOWER", "ROSE_BUSH", "PEONY", "LILAC", "MOSS_CARPET", "ROOTS",
        "HANGING_ROOTS", "SUGAR_CANE"
    );

    public static boolean isAir(BotBlockData block) {
        return block != null && AIR_TYPES.contains(block.getType().toUpperCase());
    }

    public static boolean isCover(BotBlockData block) {
        return block != null && COVER_TYPES.contains(block.getType().toUpperCase());
    }

    public static boolean isPassableForHeadroom(BotBlockData block) {
        return isAir(block) || isCover(block);
    }

    @JsonIgnore
    public static boolean isDangerous(BotBlockData block) {
        return block != null && (
            DANGEROUS_PASSABLE.contains(block.getType().toUpperCase()) ||
            DANGEROUS_IMPASSABLE.contains(block.getType().toUpperCase())
        );
    }

    public static boolean isPassableDangerous(BotBlockData block) {
        return block != null && DANGEROUS_PASSABLE.contains(block.getType().toUpperCase());
    }

    public static boolean isPassableAbove(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();
        return AIR_TYPES.contains(type) || COVER_TYPES.contains(type);
    }

    public static boolean isPassableWalkable(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();

        if (AIR_TYPES.contains(type)) return true;
        if (COVER_TYPES.contains(type)) return true;
        if (DANGEROUS_PASSABLE.contains(type)) return true;
        if (DANGEROUS_IMPASSABLE.contains(type)) return false;

        if (type.contains("LEAVES") || type.contains("FENCE") || type.contains("WALL") || type.contains("DOOR")) return false;
        if (type.contains("BAMBOO") || type.contains("BARREL") || type.contains("TRAPDOOR")) return false;
        if (type.contains("LOG") || type.contains("PLANK") || type.contains("WOOD")) return false;

        return true;
    }

    /**
     * Можно ли стоять внутри этого блока?
     */
    public static boolean canBotStandInBlock(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();

        if (AIR_TYPES.contains(type)) return true;
        if (COVER_TYPES.contains(type)) return true;

        if (DANGEROUS_PASSABLE.contains(type)) return false;
        if (DANGEROUS_IMPASSABLE.contains(type)) return false;

        if (type.contains("FENCE") || type.contains("WALL") || type.contains("DOOR")) return false;
        if (type.contains("LEAVES") || type.contains("TRAPDOOR")) return false;

        return true;
    }
}
