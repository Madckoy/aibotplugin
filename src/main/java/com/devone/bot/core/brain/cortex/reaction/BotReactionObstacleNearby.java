package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceExcavate;

import com.devone.bot.core.task.passive.BotTaskManager;

import com.devone.bot.core.utils.blocks.BlockMaterialUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.Location;

import org.bukkit.block.Block;

import java.util.Optional;

public class BotReactionObstacleNearby implements IBotReaction {
    
    private static final String ICON = "🛑";

    @Override
    public Optional<Runnable> validate(Bot bot) {

        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " 🛑 Проверка на препятствия рядом");
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " В движении: " + bot.getNPCNavigator().isNavigating());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Текущая рекомендация: "+bot.getNavigator().getSuggestion());
        BotLogger.debug(ICON, bot.isLogged(), bot.getId() + " Навигатор занят? "+bot.getNavigator().isCalculating());

        if(bot.getNPCNavigator().isNavigating()) return Optional.empty();


        BotPosition botPos = bot.getNavigator().getPosition();
        Location loc = BotWorldHelper.botPositionToWorldLocation(botPos);

        int baseX = loc.getBlockX();
        int baseY = loc.getBlockY();
        int baseZ = loc.getBlockZ();

        int[][] directions = {
            {1, 0},   // восток
            {-1, 0},  // запад
            {0, 1},   // юг
            {0, -1}   // север
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dz = dir[1];

            for (int dy = 0; dy <= 1; dy++) { // проверяем уровень ног и головы
                
                int neibX = baseX + dx;
                int neibY = baseY + dy;
                int neibZ = baseZ + dz;

                Block mcBlock = loc.getWorld().getBlockAt(neibX, neibY, neibZ);
                BotBlockData botBlock = BotWorldHelper.blockToBotBlockData(mcBlock);

                if (botBlock != null && (BlockMaterialUtils.isSafeImpassable(botBlock) || BlockMaterialUtils.isDangerousImpassable(botBlock))) {
                    return Optional.of(() -> {
                        BotTaskManager.push(bot, new BotSequenceExcavate(bot));
                    });
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return ICON+" Препятствие возле бота";
    }

    @Override
    public boolean shouldInterrupt(Bot bot) {
        return true ;
    }

}
