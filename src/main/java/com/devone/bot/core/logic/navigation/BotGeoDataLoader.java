package com.devone.bot.core.logic.navigation;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

public class BotGeoDataLoader {

    private final List<BotBlockData> blocks = new ArrayList<>();
    private BotCoordinate3D botPosition;

    public List<BotBlockData> loadFromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        for (JsonNode node : root.get("blocks")) {
            BotBlockData block = mapper.treeToValue(node, BotBlockData.class);
            blocks.add(block);
        }

        JsonNode botNode = root.get("bot_position");
        botPosition = mapper.treeToValue(botNode, BotCoordinate3D.class);

        return blocks;
    }
    public List<BotBlockData> getBlocks() {
        return blocks;
    }
    public BotCoordinate3D getBotPosition() {
        return botPosition;
    }
}