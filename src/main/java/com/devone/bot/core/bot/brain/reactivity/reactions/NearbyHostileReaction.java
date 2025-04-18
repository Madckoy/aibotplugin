
package com.devone.bot.core.bot.brain.reactivity.reactions;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.blocks.BotBlockData;
import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.bot.brain.reactivity.IBotReactionStrategy;
import com.devone.bot.core.bot.scene.BotSceneData;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class NearbyHostileReaction implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        BotSceneData sceneData = bot.getBrain().getMemory().getSceneData();
        BotLocation botPos = bot.getNavigation().getLocation();

        if (sceneData != null) {
            for (BotBlockData entity : sceneData.entities) {
                if (entity.isHostileMob()) {
                    double dist = botPos.distanceTo(entity.getLocation());
                    if (dist < 5) {
                        return Optional.of(() -> {
                            BotLogger.debug("⚠️", true, "Враг слишком близко: " + entity);
                        });
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Близость враждебного моба";
    }
}
