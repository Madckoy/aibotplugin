package com.devone.bot.core.brain.navigator.tags;

import com.devone.bot.core.utils.blocks.BlockMaterialUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;
import com.devone.bot.core.utils.blocks.BotTagUtils;

import java.util.*;

public class BotWalkableBlockTagger {

    /**
     * Помечает блоки тегами walkable:solid, walkable:cover, walkable:covered, walkable:hazard.
     * Основывается на наличии safe:block и логике окружения.
     */
    public static int tagWalkableBlocks(List<BotBlockData> blocks) {
        if (blocks == null || blocks.isEmpty()) return 0;

        Map<BotPositionKey, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData block : blocks) {
            blockMap.put(block.getPositionKey(), block);
        }

        blocks.sort(Comparator.comparingInt(BotBlockData::getY).reversed());

        for (BotBlockData block : blocks) {

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            BotBlockData above = blockMap.get(new BotPositionKey(x, y + 1, z));
            BotBlockData above2 = blockMap.get(new BotPositionKey(x, y + 2, z));
            BotBlockData above3 = blockMap.get(new BotPositionKey(x, y + 2, z));

            if (block==null || above2==null || above==null) continue; //нет места что бы стоять
            if (BlockMaterialUtils.isDangerous(block)  || 
                BlockMaterialUtils.isDangerous(above)  || 
                BlockMaterialUtils.isDangerous(above2) || 
                BlockMaterialUtils.isDangerous(above3))   continue;//над головой либо нет блоков либо опасные - не walkable

            if (BlockMaterialUtils.isNavigationObstacle(block)  || 
                BlockMaterialUtils.isNavigationObstacle(above)  || 
                BlockMaterialUtils.isNavigationObstacle(above2) || 
                BlockMaterialUtils.isNavigationObstacle(above3)) { continue; }

           
            BotBlockData below = blockMap.get(new BotPositionKey(x, y - 1, z));
            if( BlockMaterialUtils.isDangerous(above) && BlockMaterialUtils.isDangerous(above2)) continue;

            // ⚠️ Опасные, но проходимые
            if (BlockMaterialUtils.isPassableDangerous(block)) {
                if ((BlockMaterialUtils.canBotStandInside(above))
                    && below != null
                    && below.hasTag("safe:block")) {

                    block.addTag("walkable:hazard");

                    if (BlockMaterialUtils.isSolidEnoughToStandOn(below)
                        && below.getTags().stream().noneMatch(t -> t.startsWith("walkable:"))) {
                        below.addTag("walkable:covered");
                    }
                }
                continue;
            }

            // ✅ Только безопасные

            if (!block.hasTag("safe:block")) continue;

            // 🧗 Разметка climbable (лианы, лестницы и т.п.)
            if (BlockMaterialUtils.isClimbable(block) && block.hasTag("safe:block")) {
                block.addTag("walkable:climbable");
            }
            
            // Если над блоком есть cover — это covered
            if (BlockMaterialUtils.isCover(above)) {
                block.addTag("walkable:covered");
                continue;
            }

            // Если блок сам cover и над ним воздух — это cover
            if (BlockMaterialUtils.isCover(block)
                && BlockMaterialUtils.canBotStandInside(above)) {
                block.addTag("walkable:cover");
                continue;
            }

            // Если блок твердый и над ним 2 проходимых — это walkable:solid
            if (BlockMaterialUtils.isSolidEnoughToStandOn(block)
                && BlockMaterialUtils.canBotStandInside(above)
                && BlockMaterialUtils.canBotStandInside(above2)) {
                block.addTag("walkable:solid");
            }

        }

        // Возвращаем подсчёт тегированных блоков
        return BotTagUtils.getTaggedBlocks(blocks, "walkable:*").size();
    }

}
