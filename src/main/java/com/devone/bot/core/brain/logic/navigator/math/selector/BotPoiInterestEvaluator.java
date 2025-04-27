package com.devone.bot.core.brain.logic.navigator.math.selector;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.logic.navigator.context.BotNavigationContext;
import org.bukkit.util.Vector;

import java.util.List;

public class BotPoiInterestEvaluator {

    public static double evaluate(Bot bot, BotNavigationContext context, BotPosition poi, float botYaw) {
        double interest = 0.0;

        if (isInDangerousLiquid(poi)) {
            return -10000;
        }

        if (isNearDangerousLiquid(poi)) {
            interest -= 1000;
        }

        if (isNearEntity(context.entities, poi)) {
            interest += 5000;
        }

        if (isNearRareBlock(context.sliced, poi)) {
            interest += 5000;
        }

        interest += computeViewBonus(botYaw, poi, bot.getNavigator().getPosition());

        interest += Math.random() * 50;

        return interest;
    }

    private static boolean isInDangerousLiquid(BotPosition poi) {
        return BotWorldHelper.isBlockInDangerousLiquid(poi);
    }

    private static boolean isNearDangerousLiquid(BotPosition poi) {
        List<org.bukkit.block.Block> nearbyBlocks = BotWorldHelper.getNearbyBlocks(poi, 2);
        for (var block : nearbyBlocks) {
            if (BotWorldHelper.isDangerousLiquid(block)) return true;
        }
        return false;
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

    private static double computeViewBonus(float botYaw, BotPosition poi, BotPosition botPos) {
        if (botPos == null || poi == null) return 0.0;

        Vector viewDir = yawToDirection(botYaw);
        Vector toPoi = new Vector(
            poi.getX() - botPos.getX(),
            0,
            poi.getZ() - botPos.getZ()
        ).normalize();

        double dot = viewDir.dot(toPoi);

        if (dot > 0.7) {
            return 500;
        } else if (dot > 0.3) {
            return 250;
        } else {
            return -300;
        }
    }

    private static Vector yawToDirection(float yaw) {
        double radians = Math.toRadians(yaw);
        double x = -Math.sin(radians);
        double z = Math.cos(radians);
        return new Vector(x, 0, z).normalize();
    }
}
