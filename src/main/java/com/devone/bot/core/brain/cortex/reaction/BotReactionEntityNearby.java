package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceEntityNearby;
import com.devone.bot.core.brain.perseption.scene.BotSceneData;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import java.util.Optional;
import org.bukkit.Location;



public class BotReactionEntityNearby implements IBotReaction {

    private static final String ICON = "👿";

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Проверка реакции на близость враждебного моба");
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " В движении: " + bot.getNPCNavigator().isNavigating());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Текущая рекомендация: "+bot.getNavigator().getSuggestion());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Навигатор занят? "+bot.getNavigator().isCalculating());      
        
        if (bot.getNPCNavigator().isNavigating() || bot.getNavigator().isCalculating()) return Optional.empty();

        BotSceneData scene = bot.getBrain().getSceneData();
        BotPosition botPos = bot.getNavigator().getPosition();
        if (scene == null || botPos == null) return Optional.empty();

        for (BotBlockData entity : scene.entities) {
            if (!BlockUtils.isHostileEntity(entity)) continue;
            if (BotWorldHelper.isInDangerousLiquid(entity)) continue;

            double dist = botPos.distanceTo(entity.getPosition());
            if (dist > BotConstants.DEFAULT_DETECTION_RADIUS) continue;

            Location eLoc = BotWorldHelper.botPositionToWorldLocation(entity.getPosition());
            if (bot.getNPCNavigator().canNavigateTo(eLoc)) {
                BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " ❗ Обнаружен моб: " + entity.getType()
                        + " (" + String.format("%.1f", dist) + " м)");
                return Optional.of(() -> BotTaskManager.push(bot, new BotSequenceEntityNearby(bot, entity)));
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return " Presence of entity";
    }

    @Override
    public boolean shouldInterrupt(Bot bot) {
        return true ;
    }
}
