package com.devone.bot.core.brain.logic.navigator.context;

import java.util.List;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionSight;

public class BotContextMakerHelper {

    public static double estimateSafeScanRadius(BotPosition bot, List<BotBlockData> reachable) {
        if (reachable.isEmpty()) return 2;

        double sum = 0;
        double max = 0;

        for (BotBlockData b : reachable) {
            double dx = b.getX() - bot.getX();
            double dy = b.getY() - bot.getY();
            double dz = b.getZ() - bot.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            sum += dist;
            max = Math.max(max, dist);
        }

        double avg = sum / reachable.size();
        return Math.max(2, (int) Math.round((avg + max) / 2));
    }

    public static int estimateSectorCountByArea(List<BotBlockData> blocks) {
        double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        double minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BotBlockData block : blocks) {
            minX = Math.min(minX, block.getX());
            maxX = Math.max(maxX, block.getX());
            minZ = Math.min(minZ, block.getZ());
            maxZ = Math.max(maxZ, block.getZ());
        }

        double area = Math.max(1, (maxX - minX + 1) * (maxZ - minZ + 1));
        double estimated = Math.sqrt(area);
        return (int) Math.max(6, Math.min(32, estimated));
    }

    public static int estimateAdaptiveMaxTargets(List<BotBlockData> reachable, double scanRadius) {
        if (reachable == null || reachable.isEmpty()) return 0;

        double densityFactor = 0.8;
        double approxArea = Math.PI * scanRadius * scanRadius;
        int suggested = (int) Math.round(Math.min(reachable.size(), approxArea * densityFactor));

        return Math.max(1, Math.min(suggested, reachable.size()));
    }

    
    public static BotNavigationContext alignBotToMaxReachableYaw(BotPositionSight botSight, List<BotBlockData> allBlocks, double fov, int radius, int height) {
        float bestYaw = 0f;
        int maxReachable = -1;
        BotNavigationContext bestContext = new BotNavigationContext();

        for (float yaw = 0f; yaw < 360f; yaw += 1f) {
            botSight.setYaw(yaw);
            BotNavigationContext context = BotNavigationContextMaker.createSceneContext(
                botSight,
                allBlocks,
                allBlocks,
                fov,
                radius,
                height
            );

            int reachableCount = (context.reachable != null) ? context.reachable.size() : 0;

            if (reachableCount > maxReachable) {
                maxReachable = reachableCount;
                bestYaw = yaw;
                bestContext = context;
            }
        }

        bestContext.bestYaw = bestYaw;
        //System.out.printf("✅ Best yaw: %.1f° with %d reachable blocks\n", bestYaw, maxReachable);

        return bestContext;
    }

}
