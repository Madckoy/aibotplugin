package com.devone.aibot.core.utils;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BotBio3DScan {

    public static List<LivingEntity> scan3D(Bot bot, int radius) {
        Location botLoc = bot.getNPCCurrentLocation();

        return bot.getNPCEntity().getWorld().getLivingEntities().stream()
            .filter(e -> e instanceof Monster)
            .filter(e -> e != bot.getNPCEntity() && !e.isDead())
            .filter(e -> e.getLocation().distance(botLoc) <= radius)
            .sorted(Comparator.comparingDouble(e -> e.getLocation().distance(botLoc)))
            .collect(Collectors.toList());
    }

    // В будущем можно добавить методы для игроков, животных и т.п.
    // Например: scanNearbyPlayers(...), scanAllLivingEntities(...)
}
