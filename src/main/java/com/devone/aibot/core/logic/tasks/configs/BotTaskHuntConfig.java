package com.devone.aibot.core.logic.tasks.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

public class BotTaskHuntConfig extends BotAbstractLocationConfig {

    public BotTaskHuntConfig() {
        super("BotTaskHunt.yml"); // Файл конфигурации
    }

    @Override
    public void generateDefaultConfig() {
        FileConfiguration config = getConfig();

        config.set("hunt.enabled", true);
        config.set("hunt.search_radius", 15);
        config.set("hunt.pickup_loot", true);

        Set<String> defaultMobs = Set.of("ZOMBIE", "SKELETON");
        config.set("hunt.target_mobs", defaultMobs);

        super.generateDefaultConfig();
    }

    public boolean isEnabled() {
        return getConfig().getBoolean("hunt.enabled", true);
    }

    public int getSearchRadius() {
        return getConfig().getInt("hunt.search_radius", 15);
    }

    public boolean shouldPickupLoot() {
        return getConfig().getBoolean("hunt.pickup_loot", true);
    }

    public Set<EntityType> getTargetMobs() {
        Set<String> mobNames = new HashSet<>(getConfig().getStringList("hunt.target_mobs"));
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
