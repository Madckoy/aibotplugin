package com.devone.bot.core.utils.blocks;

import java.util.ArrayList;
import java.util.List;

public class BotTagUtils {

    public static List<BotBlockData> getTaggedBlocks(List<BotBlockData> tagged, String tagQuery) {
        return findTaggedBlocks(tagged, tagQuery);
    }

    private static List<BotBlockData> findTaggedBlocks(List<BotBlockData> blocks, String tagQuery) {
        if (blocks == null || tagQuery == null || tagQuery.isBlank()) return List.of();

        String[] filters = tagQuery.split(",");
        List<TagFilter> tagFilters = new ArrayList<>();

        for (String filter : filters) {
            String trimmed = filter.trim();
            boolean isWildcard = trimmed.endsWith("*");
            String baseTag = isWildcard ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
            tagFilters.add(new TagFilter(baseTag, isWildcard));
        }

        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData block : blocks) {
            boolean allMatch = true;

            for (TagFilter tf : tagFilters) {
                boolean matched = false;
                for (String tag : block.getTags()) {
                    if ((tf.isWildcard && tag.startsWith(tf.baseTag)) || (!tf.isWildcard && tag.equals(tf.baseTag))) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                result.add(block);
            }
        }

        return result;
    }


    private static class TagFilter {
        String baseTag;
        boolean isWildcard;

        TagFilter(String baseTag, boolean isWildcard) {
            this.baseTag = baseTag;
            this.isWildcard = isWildcard;
        }
    }
}
