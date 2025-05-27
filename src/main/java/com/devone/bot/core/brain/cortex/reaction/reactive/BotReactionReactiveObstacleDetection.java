package com.devone.bot.core.brain.cortex.reaction.reactive;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.reaction.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceContainerExcavate;

import com.devone.bot.core.task.passive.BotTaskManager;

import com.devone.bot.core.utils.blocks.BlockMaterialUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;


import com.devone.bot.core.utils.world.BotWorldHelper;

import org.bukkit.Location;

import org.bukkit.block.Block;

import java.util.Optional;

public class BotReactionReactiveObstacleDetection implements IBotReaction {

    @Override
    public Optional<Runnable> validate(Bot bot) {
        if (bot.getNavigator().getPosition() == null) return Optional.empty();

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
                int neibY = baseY;
                int neibZ = baseZ+dz;

                BotBlockData botBlock = new BotBlockData(neibX,neibY,neibZ);

                Block mcBlock = BotWorldHelper.botBlockDataToWorldBlock(botBlock);
                botBlock = BotWorldHelper.blockToBotBlockData(mcBlock);

                if (botBlock != null && !BlockMaterialUtils.AIR_TYPES.contains(botBlock.getType())) {
                    return Optional.of(() -> {
                        BotTaskManager.push(bot, new BotSequenceContainerExcavate(bot));
                    });
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "⛏️ Препятствие возле бота";
    }

}
