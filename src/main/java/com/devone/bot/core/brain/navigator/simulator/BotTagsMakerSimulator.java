package com.devone.bot.core.brain.navigator.simulator;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.brain.navigator.tags.BotNavigationTagsMaker;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionSight;
import com.devone.bot.core.utils.blocks.BotTagUtils;

public class BotTagsMakerSimulator {

    public static float reachableFindBestYaw(BotPositionSight bot, List<BotBlockData> allBlocks, double fov, int radius, int height) {
        float bestYaw = 0f;
        int bestReachable = -1;

          for (int yawInt = 359; yawInt >= 0; yawInt--) {
            float yaw = (float) yawInt;

            BotPositionSight botCopy = new BotPositionSight(
                bot.getX(), bot.getY(), bot.getZ(),
                yaw,
                bot.getPitch()
            );

            List<BotBlockData> blocksCopy = new ArrayList<>();
            for (BotBlockData block : allBlocks) {
                BotBlockData cloned = block.clone(); // Убедись, что clone() копирует position, UUID, type, tags
                cloned.getTags().removeIf(tag -> tag.startsWith("reachable:") || tag.startsWith("fov:") || tag.startsWith("navigation:"));
                blocksCopy.add(cloned);
            }

            BotNavigationTagsMaker.tagReachableBlocks(
                botCopy,
                blocksCopy,
                fov,
                radius,
                height
            );

            List<BotBlockData> reachable = BotTagUtils.getTaggedBlocks(blocksCopy, "reachable:block");

            if (reachable.size() > bestReachable) {
                bestReachable = reachable.size();
                bestYaw = yaw;
            }
        }

        //System.out.printf("Yaw: %.0f => Reachable: %d\n", bestYaw, bestReachable);
        return bestYaw;
    }

}
