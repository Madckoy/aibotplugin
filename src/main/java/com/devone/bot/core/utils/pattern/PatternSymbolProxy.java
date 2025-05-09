package com.devone.bot.core.utils.pattern;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class PatternSymbolProxy {

    private final Map<Character, Material> proxyLegend = new HashMap<>();
    private final Map<String, Character> emojiToProxy = new HashMap<>();
    private final Map<Character, String> proxyToEmoji = new HashMap<>();
    
    private char nextProxyChar = 'A'; // Начинаем с A, B, C и т.д.

    public PatternSymbolProxy(Map<String, Material> emojiLegend) {
        for (Map.Entry<String, Material> entry : emojiLegend.entrySet()) {
            String emoji = entry.getKey();
            Material material = entry.getValue();
            register(emoji, material);
        }
    }

    private void register(String emoji, Material material) {
        Character proxyChar = nextProxyChar++;

        emojiToProxy.put(emoji, proxyChar);
        proxyToEmoji.put(proxyChar, emoji);
        proxyLegend.put(proxyChar, material);
    }

    public Material getMaterialByProxy(char proxy) {
        return proxyLegend.get(proxy);
    }

    public Character getProxyByEmoji(String emoji) {
        return emojiToProxy.get(emoji);
    }

    public String getEmojiByProxy(Character proxy) {
        return proxyToEmoji.get(proxy);
    }

    public Map<Character, Material> getProxyLegend() {
        return proxyLegend;
    }

    
}
