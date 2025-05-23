package com.devone.bot.core.brain.navigator.tags;

import com.devone.bot.core.utils.blocks.BlockMaterialUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotWalkableBlockTagger {

    /**
     * Тегирует блоки как walkable:solid, walkable:cover, walkable:covered, walkable:hazard
     * на основе уже проставленных тегов "safe:block".
     * Возвращает количество walkable-блоков.
     */
    public static int tagWalkableBlocks(List<BotBlockData> blocks) {
        if (blocks == null || blocks.isEmpty()) return 0;

        Map<BotPositionKey, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData block : blocks) {
            blockMap.put(block.toKey(), block);
        }

        int count = 0;

        for (BotBlockData block : blocks) {

            if (BlockMaterialUtils.isAir(block)) continue;

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            BotBlockData above = blockMap.get(new BotPositionKey(x, y + 1, z));
            BotBlockData below = blockMap.get(new BotPositionKey(x, y - 1, z));

            // Пропустить уже размеченные
            if (block.getTags().stream().anyMatch(tag -> tag.startsWith("walkable:"))) {
                continue;
            }

            // Опасные, но проходимые
            if (BlockMaterialUtils.isPassableDangerous(block)) {
                if (BlockMaterialUtils.isAir(above) &&
                    below != null &&
                    below.hasTag("safe:block")) {

                    block.addTag("walkable:hazard");
                    count++;

                    if (!BlockMaterialUtils.isAir(below) &&
                        !BlockMaterialUtils.isCover(below) &&
                        below.getTags().stream().noneMatch(t -> t.startsWith("walkable:"))) {
                        below.addTag("walkable:covered");
                        count++;
                    }
                }
                continue;
            }

            // Остальное — только безопасные
            if (!block.hasTag("safe:block")) continue;
            // ✅ ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА НА ПРОХОДИМОСТЬ
            if (!BlockMaterialUtils.isPassableWalkable(block)) continue;

            if (BlockMaterialUtils.isCover(block)) {
                if (BlockMaterialUtils.isAir(above)) {
                    block.addTag("walkable:cover");
                    count++;

                    if (below != null &&
                        !BlockMaterialUtils.isAir(below) &&
                        !BlockMaterialUtils.isCover(below) &&
                        below.hasTag("safe:block") &&
                        below.getTags().stream().noneMatch(t -> t.startsWith("walkable:"))) {

                        below.addTag("walkable:covered");
                        count++;
                    }
                }
            } else {
                BotBlockData above2 = blockMap.get(new BotPositionKey(x, y + 2, z));
                //boolean headroom = BlockMaterialUtils.isAir(above) && BlockMaterialUtils.isAir(above2);
                boolean headroom = BlockMaterialUtils.isPassableAbove(above) && BlockMaterialUtils.isPassableAbove(above2);

                if (headroom) {
                    block.addTag("walkable:solid");
                    count++;
                }
            }
        }

        return count;
    }
}
