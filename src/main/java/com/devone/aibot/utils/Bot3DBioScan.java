package com.devone.aibot.utils;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Bot3DBioScan {

    public static List<LivingEntity> scan3D(Bot bot, int radius) {

        if(bot.getNPCEntity()==null) {return new ArrayList<LivingEntity>();}

        Location botLoc = bot.getRuntimeStatus().getCurrentLocation();

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
