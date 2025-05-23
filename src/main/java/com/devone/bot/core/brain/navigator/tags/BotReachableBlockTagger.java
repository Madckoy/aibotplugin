package com.devone.bot.core.brain.navigator.tags;

import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPositionKey;
import com.devone.bot.core.utils.blocks.BotPositionSight;

import java.util.*;

public class BotReachableBlockTagger {

    private static final List<int[]> OFFSETS = new ArrayList<>();
    static {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    OFFSETS.add(new int[]{dx, dy, dz});
                }
            }
        }
    }

    public static int tagReachableBlocks(List<BotBlockData> blocks, BotPositionSight botPos) {
        if (blocks == null || blocks.isEmpty()) return 0;

        Map<BotPositionKey, BotBlockData> blockMap = new HashMap<>();
        Set<BotPositionKey> walkableSet = new HashSet<>();

        for (BotBlockData block : blocks) {
            if (hasWalkableTag(block)) {
                BotPositionKey key = block.toKey();
                walkableSet.add(key);
                blockMap.put(key, block);
            }
        }

        if (walkableSet.isEmpty()) return 0;

        // Получаем округлённую позицию бота (вниз)
        int startX = (int) Math.floor(botPos.getX());
        int startY = (int) Math.floor(botPos.getY());
        int startZ = (int) Math.floor(botPos.getZ());

        // Ищем ближайший валидный walkable блок в радиусе по Y (от -2 до +2)
        BotPositionKey start = null;
        for (int dy = -2; dy <= 2; dy++) {
            BotPositionKey candidate = new BotPositionKey(startX, startY + dy, startZ);
            if (walkableSet.contains(candidate)) {
                start = candidate;
                break;
            }
        }

        // Если не нашли — выходим
        if (start == null) return 0;

        // Запускаем flood fill
        Queue<BotPositionKey> queue = new LinkedList<>();
        queue.add(start);

        if (queue.isEmpty()) return 0;

        while (!queue.isEmpty()) {
            BotPositionKey currentKey = queue.poll();
            BotBlockData currentBlock = blockMap.get(currentKey);
            if (currentBlock == null || currentBlock.hasTag("reachable:temp")) continue;

            currentBlock.addTag("reachable:temp");

            if (currentBlock.hasTag("fov:slice")) {
                currentBlock.addTag("reachable:block");
            }

            for (int[] offset : OFFSETS) {
                int dx = offset[0];
                int dy = offset[1];
                int dz = offset[2];

                BotPositionKey neighborKey = new BotPositionKey(
                    currentKey.getX() + dx,
                    currentKey.getY() + dy,
                    currentKey.getZ() + dz
                );

                if (!walkableSet.contains(neighborKey)) continue;

                BotBlockData neighborBlock = blockMap.get(neighborKey);
                if (neighborBlock == null || neighborBlock.hasTag("reachable:temp")) continue;

                if (Math.abs(neighborBlock.getY() - currentBlock.getY()) > 1) continue;

                // Диагональная фильтрация
                if (Math.abs(dx) + Math.abs(dz) == 2) {
                    BotPositionKey side1 = new BotPositionKey(currentKey.getX() + dx, currentKey.getY(), currentKey.getZ());
                    BotPositionKey side2 = new BotPositionKey(currentKey.getX(), currentKey.getY(), currentKey.getZ() + dz);

                    boolean side1Walkable = walkableSet.contains(side1);
                    boolean side2Walkable = walkableSet.contains(side2);

                    if (!side1Walkable && !side2Walkable) continue;
                }

                queue.add(neighborKey);
            }
        }

        // Вторичный проход: пометить блоки под walkable:cover с fov:slice
        for (BotBlockData block : blocks) {
            if (!block.hasTag("fov:slice")) continue;
            if (!block.hasTag("walkable:cover")) continue;

            BotPositionKey currentKey = block.toKey();
            BotPositionKey belowKey = new BotPositionKey(currentKey.getX(), currentKey.getY() - 1, currentKey.getZ());

            BotBlockData belowBlock = blockMap.get(belowKey);
            if (belowBlock == null || belowBlock.hasTag("reachable:block")) continue;

            if (hasWalkableTag(belowBlock)) {
                belowBlock.addTag("reachable:block");
            }
        }

        // Убираем reachable:block с блока в ногах бота
        BotPositionKey legsBotKey = new BotPositionKey(startX, startY, startZ);
        BotBlockData legsBot = blockMap.get(legsBotKey);
        if (legsBot != null) {
            legsBot.getTags().remove("reachable:block");
        }

        // Убираем reachable:block с блока под ботом
        BotPositionKey underBotKey = new BotPositionKey(startX, startY - 1, startZ);
        BotBlockData underBot = blockMap.get(underBotKey);
        if (underBot != null) {
            underBot.getTags().remove("reachable:block");
        }

        // Финальный проход — считаем количество reachable:block
        int count = 0;
        for (BotBlockData block : blocks) {
            if (block.getTags().contains("reachable:block")) {
                count++;
            }
        }

        // Очистка временного тега
        for (BotBlockData block : blocks) {
            block.getTags().remove("reachable:temp");
        }

        return count;
    }

    private static boolean hasWalkableTag(BotBlockData block) {
        if(block.hasTag("walkable:climbable")) return false;
        
        return block.hasTag("walkable:solid")   ||
               block.hasTag("walkable:cover")   ||
               block.hasTag("walkable:covered") ||
               block.hasTag("walkable:hazard"); 
    }
} 
