package com.devone.bot.core.utils.blocks;

import java.util.ArrayList;

import java.util.List;


public class BotTagUtils {

    public static List<BotBlockData> getTaggedBlocks(List<BotBlockData>  tagged, String tagQuery) {
        return findTaggedBlocks(tagged, tagQuery);
    }

    private static List<BotBlockData> findTaggedBlocks(List<BotBlockData> blocks, String tagQuery) {
        if (blocks == null || tagQuery == null || tagQuery.isEmpty()) return List.of();

        boolean isWildcard = tagQuery.endsWith(":*");
        String baseTag = isWildcard ? tagQuery.substring(0, tagQuery.length() - 1) : tagQuery;

        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData block : blocks) {
            for (String tag : block.getTags()) {
                if ((isWildcard && tag.startsWith(baseTag)) || (!isWildcard && tag.equals(baseTag))) {
                    result.add(block);
                    break;
                }
            }
        }

        return result;
    }
}
