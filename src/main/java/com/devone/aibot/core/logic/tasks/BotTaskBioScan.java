package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BotTaskBioScan extends BotTask {

    private int scanRadius = 10;
    private List<LivingEntity> foundTargets;

    public BotTaskBioScan(Bot bot, int scanRadius) {
        super(bot, "🧠");
        this.scanRadius = scanRadius;
    }

    public List<LivingEntity> getFoundTargets() {
        return foundTargets;
    }

    @Override
    public void executeTask() {
        setObjective("Scanning for lifeforms...");

        foundTargets = bot.getNPCEntity().getWorld().getLivingEntities().stream()
            .filter(e -> e.getLocation().distance(bot.getNPCCurrentLocation()) <= scanRadius)
            .filter(e -> e instanceof Monster)
            .filter(e -> e != bot.getNPCEntity() && !e.isDead())
            .sorted(Comparator.comparingDouble(e -> e.getLocation().distance(bot.getNPCCurrentLocation())))
            .collect(Collectors.toList());

        BotLogger.debug("🧠 BioScan found " + foundTargets.size() + " targets.");

        // Можно сохранить в бота или передать куда надо
        bot.setDetectedEntities(foundTargets);

        isDone = true;
    }
}
