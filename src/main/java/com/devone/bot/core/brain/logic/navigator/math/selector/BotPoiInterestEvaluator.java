package com.devone.bot.core.brain.logic.navigator.math.selector;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;

import org.bukkit.block.Block;

import java.util.List;

public class BotPoiInterestEvaluator {

    public static double evaluate(Bot bot, BotPosition poi, BotNavigationContext context) {
        double interest = 0.0;

        if (isInDangerousLiquid(poi)) {
            return -10000; // <- сразу даём штраф и выходим
        }
    
        // Добавляем динамический штраф за близость к воде
        interest += computeDangerousLiquidPenalty(poi);
    
        if (isNearEntity(context.entities, poi)) {
            interest += 5000;
        }
    
        if (isNearRareBlock(context.blocks, poi)) {
            interest += 1000;
        }
        
        return interest;
    }
    

    private static boolean isInDangerousLiquid(BotPosition poi) {
        return BotWorldHelper.isBlockInDangerousLiquid(poi);
    }

    private static boolean isNearEntity(List<BotBlockData> entities, BotPosition poi) {
        if (entities == null) return false;
        for (var entity : entities) {
            if (poi.distanceTo(entity.getPosition()) <= 5.0) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNearRareBlock(List<BotBlockData> sliced, BotPosition poi) {
        if (sliced == null) return false;
        for (var block : sliced) {
            if (poi.distanceTo(block.getPosition()) <= 5.0) {
                return true;
            }
        }
        return false;
    }

    private static double computeDangerousLiquidPenalty(BotPosition poi) {
    List<Block> nearbyBlocks = BotWorldHelper.getNearbyBlocks(poi, 5);
    double maxPenalty = 0;

    for (var block : nearbyBlocks) {
        if (BotWorldHelper.isDangerousLiquid(block)) {
            double distance = poi.distanceTo(BotWorldHelper.locationToBotPosition(block.getLocation()));
            if (distance <= 1.0) {
                maxPenalty = Math.min(maxPenalty, -7000);
            } else if (distance <= 2.0) {
                maxPenalty = Math.min(maxPenalty, -5000);
            } else if (distance <= 3.0) {
                maxPenalty = Math.min(maxPenalty, -3000);
            } else if (distance <= 5.0) {
                maxPenalty = Math.min(maxPenalty, -1000);
            }
        }
    }
    return maxPenalty;
}

}
