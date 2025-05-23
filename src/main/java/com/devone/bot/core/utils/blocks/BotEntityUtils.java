package com.devone.bot.core.utils.blocks;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.EnumSet;
import java.util.Set;

public class BotEntityUtils {

    private static final Set<EntityType> HOSTILE_MOBS = EnumSet.of(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.CREEPER,
        EntityType.SPIDER,
        EntityType.CAVE_SPIDER,
        EntityType.ENDERMAN,
        EntityType.HUSK,
        EntityType.DROWNED,
        EntityType.STRAY,
        EntityType.WITCH,
        EntityType.SLIME,
        EntityType.MAGMA_CUBE,
        EntityType.PILLAGER,
        EntityType.VINDICATOR,
        EntityType.EVOKER,
        EntityType.RAVAGER,
        EntityType.PHANTOM,
        EntityType.WARDEN,
        EntityType.ZOGLIN
    );

    private static final Set<EntityType> PEACEFULL_MOBS = EnumSet.of(
        EntityType.SHEEP,
        EntityType.COW,
        EntityType.CHICKEN,
        EntityType.PIG,
        EntityType.RABBIT,
        EntityType.VILLAGER,
        EntityType.IRON_GOLEM,
        EntityType.SNOWMAN,
        EntityType.HORSE,
        EntityType.DONKEY,
        EntityType.LLAMA,
        EntityType.CAT,
        EntityType.WOLF,
        EntityType.OCELOT,
        EntityType.FROG,
        EntityType.FOX,
        EntityType.MUSHROOM_COW
    );

    public static boolean isHostile(LivingEntity entity) {
        return HOSTILE_MOBS.contains(entity.getType());
    }

    public static boolean isPeacefull(LivingEntity entity) {
        return PEACEFULL_MOBS.contains(entity.getType());
    }


    public static boolean isHostile(String type) {
        try {
            EntityType entityType = EntityType.valueOf(type.toUpperCase());
            return HOSTILE_MOBS.contains(entityType);
        } catch (IllegalArgumentException e) {
            return false; // строка не соответствует ни одному EntityType
        }
    }
    
    public static boolean isPeaceful(String type) {
        try {
            EntityType entityType = EntityType.valueOf(type.toUpperCase());
            return PEACEFULL_MOBS.contains(entityType);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}