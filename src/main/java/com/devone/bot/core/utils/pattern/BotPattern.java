package com.devone.bot.core.utils.pattern;

import com.devone.bot.core.utils.blocks.BotPosition;
import java.util.List;
import java.util.Map;

public class BotPattern {
    private BotPosition offset;
    private Map<Integer, List<String>> layers;

    public BotPosition getOffset() { return offset; }
    public void setOffset(BotPosition offset) { this.offset = offset; }

    public Map<Integer, List<String>> getLayers() { return layers; }
    public void setLayers(Map<Integer, List<String>> layers) { this.layers = layers; }
}
