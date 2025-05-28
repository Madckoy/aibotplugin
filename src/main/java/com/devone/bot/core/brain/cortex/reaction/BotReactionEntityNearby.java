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

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " 😈 Проверка реакции на близость враждебного моба");

        BotSceneData scene = bot.getBrain().getSceneData();
        BotPosition botPos = bot.getNavigator().getPosition();

        if (scene != null) {
            for (BotBlockData entity : scene.entities) {
                if (BlockUtils.isHostileEntity(entity)==false)
                    continue;
                if (BotWorldHelper.isInDangerousLiquid(entity))
                    continue;

                double dist = botPos.distanceTo(entity.getPosition());
                if (dist < BotConstants.DEFAULT_DETECTION_RADIUS)
                    continue;

                BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " ❗ Обнаружен моб: " + entity.getType()
                        + " (" + String.format("%.1f", dist) + " м)");

                Location eLoc = BotWorldHelper.botPositionToWorldLocation(entity.getPosition());
                
                boolean canNavigate = bot.getNPCNavigator().canNavigateTo(eLoc);
                
                if(canNavigate) {
                    return Optional.of(() -> {
                        BotTaskManager.push(bot, new BotSequenceEntityNearby(bot, entity));
                    });
                }

            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "😈 Близость враждебного моба";
    }
}
