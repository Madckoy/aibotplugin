package com.devone.bot.core.brain.navigator.selector;

import java.util.List;
import java.util.Random;

import com.devone.bot.core.utils.blocks.BlockUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionSight;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotBestTargetSelector {

    public static BotBlockData selectBestInYawCone(BotPositionSight botPos, List<BotBlockData> blocks, float yawConeWidth) {
        BotBlockData best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        float yawCenter = botPos.getYaw();
        float halfFov = yawConeWidth / 2.0f;

        for (BotBlockData block : blocks) {
            double distance = BlockUtils.distanceXZ(botPos.toBlockData(), block);
            if (distance < 2.0 || distance > 12.0) continue;

            float yawToBlock = BotWorldHelper.calculateYawTo(botPos.toBlockData(), block);
            float yawDiff = Math.abs(normalizeYaw(yawToBlock - yawCenter));
            yawDiff = Math.min(yawDiff, 360 - yawDiff);

            if (yawDiff > halfFov) continue;

            double distScore = Math.exp(-Math.pow((distance - 6.0) / 2.5, 2));
            double yawScore = Math.exp(-Math.pow(yawDiff / (halfFov / 2.0), 2));

            double score = distScore * yawScore;

            if (score > bestScore) {
                bestScore = score;
                best = block;
            }
        }

        return best;
    }

    public static BotBlockData selectRandom(List<BotBlockData> candidates) {
        Random random = new Random();
        if (candidates == null || candidates.isEmpty())
            return null;
        return candidates.get(random.nextInt(candidates.size()));
    }


    private static float normalizeYaw(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;
        return yaw;
    }

}
