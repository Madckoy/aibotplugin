package com.devone.bot.core.bot.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

public class BlockMaterialUtils {

    public static final Set<String> AIR_TYPES = Set.of("AIR", "CAVE_AIR", "VOID_AIR");

    public static Set<Material> dirtTypes = Set.of(
                                                    Material.DIRT,
                                                    Material.GRASS_BLOCK,
                                                    Material.PODZOL,
                                                    Material.MYCELIUM,
                                                    Material.COARSE_DIRT,
                                                    Material.ROOTED_DIRT
                                                );

    // Блоки, которые представляют угрозу (например, наносят урон или мешают движению)
    public static final Set<String> UNSAFE_TYPES = Set.of(
        "WATER", "LAVA", "CACTUS", "FIRE", "MAGMA_BLOCK", "CAMPFIRE", "SOUL_CAMPFIRE",
        "WITHER_ROSE", "SWEET_BERRY_BUSH", "DRIPSTONE_BLOCK", "POINTED_DRIPSTONE",
        "POWDER_SNOW", "END_PORTAL", "NETHER_PORTAL", "VOID_AIR", "UNKNOWN"
    );

    public static final Set<String> COVER_TYPES = Set.of(
        "ICE",
        "SNOW",
        "CARPET",
        "TALL_GRASS",
        "GRASS",
        "FERN",
        "LARGE_FERN",
        "DEAD_BUSH",
        "SEAGRASS",
        "TALL_SEAGRASS",
        "FLOWER",
        "DANDELION",
        "POPPY",
        "BLUE_ORCHID",
        "ALLIUM",
        "AZURE_BLUET",
        "RED_TULIP",
        "ORANGE_TULIP",
        "WHITE_TULIP",
        "PINK_TULIP",
        "OXEYE_DAISY",
        "CORNFLOWER",
        "LILY_OF_THE_VALLEY",
        "SUNFLOWER",
        "ROSE_BUSH",
        "PEONY",
        "LILAC",
        "MOSS_CARPET",
        "ROOTS",
        "HANGING_ROOTS",
        "SUGAR_CANE"
    );

    private static final Map<String, String> MATERIAL_COLOR_MAP = createColorMap();

    public static boolean isAir(String type ) {
        return type != null && BlockMaterialUtils.AIR_TYPES.contains(type.toUpperCase());
    } 

    private static Map<String, String> createColorMap() {
        Map<String, String> map = new HashMap<>();
        map.put("AIR", "#e0e0e0");
        map.put("STONE", "#808080");
        map.put("COBBLESTONE", "#696969");
        map.put("DIRT", "#a0522d");
        map.put("GRASS", "#228b22");
        map.put("GRASS_BLOCK", "#228b22");
        map.put("SAND", "#f4e99d");
        map.put("RED_SAND", "#d47032");
        map.put("WATER", "#1e90ff");
        map.put("LAVA", "#ff4500");
        map.put("SNOW", "#ffffff");
        map.put("ICE", "#aee3f7");
        map.put("WOOD", "#deb887");
        map.put("OAK_LOG", "#8b5a2b");
        map.put("OAK_PLANKS", "#c19a6b");
        map.put("MAGMA_BLOCK", "#ff8c00");
        map.put("CACTUS", "#228b22");
        map.put("FLOWER", "#ff69b4");
        map.put("DANDELION", "#ffff00");
        map.put("POPPY", "#ff0000");
        // можно добавлять и другие
        return map;
    }

    public static String getColorCodeForType(String type) {
        if (type == null) return "#cccccc";
        String t = type.toUpperCase();

        // сначала пытаемся прямое соответствие
        if (MATERIAL_COLOR_MAP.containsKey(t)) return MATERIAL_COLOR_MAP.get(t);

        // затем пытаемся найти по вхождению (например, GRASS_PATH, STONE_BRICKS и т.д.)
        for (Map.Entry<String, String> entry : MATERIAL_COLOR_MAP.entrySet()) {
            if (t.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "#aaaaaa"; // fallback
    }




}