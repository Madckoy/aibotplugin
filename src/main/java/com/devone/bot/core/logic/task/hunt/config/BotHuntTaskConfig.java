package com.devone.bot.core.logic.task.hunt.config;

import org.bukkit.entity.EntityType;

import com.devone.bot.core.logic.task.explore.config.BotExploreTaskConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BotHuntTaskConfig extends BotExploreTaskConfig {

    public BotHuntTaskConfig() {
        super(BotHuntTaskConfig.class.getSimpleName());
    }

    @Override
    public void generateDefaultConfig() {
        config.set("hunt.pickup_loot", true);

        List<String> defaultAggressiveMobs = List.of("ZOMBIE", "SKELETON");
        config.set("hunt.aggressive_mobs", defaultAggressiveMobs);

        List<String> defaultPassiveMobs = List.of("COW", "PIG", "CHICKEN", "SHEEP");
        config.set("hunt.passive_mobs", defaultPassiveMobs);

        super.generateDefaultConfig();
    }

    public boolean shouldPickupLoot() {
        return config.getBoolean("hunt.pickup_loot", true);
    }

    public Set<EntityType> getTargetAggressiveMobs() {
        List<String> mobNames = getConfig().getStringList("hunt.aggressive_mobs");
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
