package com.devone.bot.core.utils.blocks;

public class BotScanInfo {
    private int radius;
    private int height;

    public BotScanInfo() {} // for JSON

    public BotScanInfo(int radius, int height) {
        this.radius = radius;
        this.height = height;
    }

    public int getRadius() { return radius; }
    public int getHeight() { return height; }
}

