package com.devone.bot.core.brain.navigator.simulator;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.brain.navigator.tags.BotNavigationTagsMaker;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionSight;
import com.devone.bot.core.utils.blocks.BotTagUtils;

public class BotTagsMakerSimulator {

    public static BotSimulatorResult reachableFindBestYaw(BotPositionSight bot, List<BotBlockData> allBlocks, double fov, int radius, int height) {
        List<BotBlockData> clearBlocks = new ArrayList<>();
        for(int i=0;i<allBlocks.size(); i++) {
            BotBlockData original = allBlocks.get(i);
            BotBlockData cloned = original.clone();
            cloned.setTags(new ArrayList<>());
            clearBlocks.add(cloned);
        }

        BotNavigationTagsMaker.tagWalkableBlocks(clearBlocks);
        int bestReachable = -1;
        BotSimulatorResult res = new BotSimulatorResult();

          for (int yawInt = 0; yawInt < 360; yawInt++) {
            float yaw = (float) yawInt;

            BotPositionSight botCopy = new BotPositionSight(
                bot.getX(), bot.getY(), bot.getZ(),
                yaw,
                bot.getPitch()
            );

            List<BotBlockData> blocksCopy = new ArrayList<>();
            for (BotBlockData block : clearBlocks) {
                BotBlockData cloned = block.clone(); 
                blocksCopy.add(cloned);
            }

            BotNavigationTagsMaker.tagReachableBlocks(
                botCopy,
                blocksCopy,
                fov,
                radius,
                height
            );

            List<BotBlockData> reachableBlocks = BotTagUtils.getTaggedBlocks(blocksCopy, "reachable:block");

            if (reachableBlocks.size() > bestReachable) {
                bestReachable = reachableBlocks.size();               
                res.status = true;
                res.yaw = yaw;
                res.reachables = bestReachable;
            }
        }
      
        return res;
    }

}
