package com.devone.bot.core.utils.pattern;

import com.devone.bot.core.utils.blocks.BotPosition;
import java.util.List;
import java.util.Map;

public class BotPattern {
    private BotPosition offset;
    private Map<Integer, List<String>> layers;
    private Map<String, String> legend; // Новый: эмодзи/символ ➔ Material
    private Map<String, String> proxy;  // Новый: прокси ➔ эмодзи/символ

    public BotPosition getOffset() { return offset; }
    public void setOffset(BotPosition offset) { this.offset = offset; }

    public Map<Integer, List<String>> getLayers() { return layers; }
    public void setLayers(Map<Integer, List<String>> layers) { this.layers = layers; }

    public Map<String, String> getLegend() { return legend; }
    public void setLegend(Map<String, String> legend) { this.legend = legend; }

    public Map<String, String> getProxy() { return proxy; }
    public void setProxy(Map<String, String> proxy) { this.proxy = proxy; }
}
