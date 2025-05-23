package com.devone.bot.core.brain.navigator.tags;

import com.devone.bot.core.utils.blocks.BlockMaterialUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;

import java.util.List;

public class BotSafeBlockTagger {

    public static int tagSafeBlocks(List<BotBlockData> blocks) {
        if (blocks == null || blocks.isEmpty()) return 0;

        int count = 0;

        for (BotBlockData block : blocks) {
            if (BlockMaterialUtils.isAir(block)) continue;

            if (!BlockMaterialUtils.isDangerous(block)) {
                block.addTag("safe:block");
                count++;
            }
        }

        return count;
    }
}
