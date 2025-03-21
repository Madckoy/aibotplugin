package com.devone.aibot.core.logic.tasks.configs;

import org.bukkit.entity.EntityType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BotTaskHuntConfig extends BotTaskExploreConfig {

    public BotTaskHuntConfig() {
        super("BotTaskHunt.yml");
    }

    @Override
    public void generateDefaultConfig() {
        config.set("hunt.pickup_loot", true);

        List<String> defaultAgressiveMobs = List.of("ZOMBIE", "SKELETON");
        config.set("hunt.agressive_mobs", defaultAgressiveMobs);

        List<String> defaultPassiveMobs = List.of("COW", "PIG", "CHICKEN", "SHEEP");
        config.set("hunt.passive_mobs", defaultPassiveMobs);

        super.generateDefaultConfig();
    }

    public boolean shouldPickupLoot() {
        return config.getBoolean("hunt.pickup_loot", true);
    }

    public Set<EntityType> getTargetAgressiveMobs() {
        List<String> mobNames = getConfig().getStringList("hunt.agressive_mobs");
        Set<EntityType> targetMobs = new HashSet<>();

        for (String name : mobNames) {
            try {
                targetMobs.add(EntityType.valueOf(name));
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ Ошибка: Некорректный тип моба в конфиге: " + name);
            }
        }
        return targetMobs;
    }

    public Set<EntityType> getTargetPassiveMobs() {
        List<String> mobNames = getConfig().getStringList("hunt.passive_mobs");
        Set<EntityType> targetMobs = new HashSet<>();

        for (String name : mobNames) {
            try {
                targetMobs.add(EntityType.valueOf(name));
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ Ошибка: Некорректный тип моба в конфиге: " + name);
            }
        }
        return targetMobs;
    }
}
