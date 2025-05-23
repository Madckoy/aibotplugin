package com.devone.bot.core.utils.blocks;

import java.util.Set;

public class BlockMaterialUtils {

    // --- Базовые типы блоков по поведению ---
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
        "HANGING_ROOTS", "SUGAR_CANE", "VINE"
    );

    // --- Определения поведения ---
    public static boolean isClimbable(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();
        return type.contains("VINE") || type.contains("LADDER") || type.contains("SCAFFOLDING");
    }

    // Можно ли зайти внутрь блока (он не мешает движению)?
    public static boolean isPassableForMovement(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();
        return AIR_TYPES.contains(type)
            || COVER_TYPES.contains(type)
            || DANGEROUS_PASSABLE.contains(type);
    }

    // Можно ли завершить движение в этом блоке (стоять внутри)?
    public static boolean canBotStandInside(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();

        // Бот может стоять в воздухе, траве, воде, но не в огне/лаве
        if (AIR_TYPES.contains(type)) return true;
        if (COVER_TYPES.contains(type)) return true;

        // В воде стоять — ок, в лаве/огне — нет
        if (type.equals("WATER") || type.equals("POWDER_SNOW")) return true;

        return false;
    }

    // Можно ли стоять на этом блоке (он держит)?
    public static boolean isSolidEnoughToStandOn(BotBlockData block) {
        if (block == null) return false;
        String type = block.getType().toUpperCase();

        if (AIR_TYPES.contains(type)) return false;
        if (COVER_TYPES.contains(type)) return false;

        // Вода и лава не держат
        if (type.equals("WATER") || type.equals("LAVA")) return false;

        // Исключаем нестабильные
        if (type.contains("FENCE") || type.contains("WALL") || type.contains("DOOR")
            || type.contains("TRAPDOOR") || type.contains("BAMBOO") || type.contains("BARREL")) {
            return false;
        }

        // Листва, камень, доски, земля и т.п. — держат
        return true;
    }

    public static boolean isCover(BotBlockData block) {
        return block != null && COVER_TYPES.contains(block.getType().toUpperCase());
    }

    public static boolean isAir(BotBlockData block) {
        return block != null && AIR_TYPES.contains(block.getType().toUpperCase());
    }

    public static boolean isDangerous(BotBlockData block) {
        if (block == null) return false;
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
}
