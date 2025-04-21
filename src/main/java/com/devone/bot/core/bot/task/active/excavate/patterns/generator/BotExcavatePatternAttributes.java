package com.devone.bot.core.bot.task.active.excavate.patterns.generator;

public class BotExcavatePatternAttributes {
    
    private final int offsetOuterX;
    private final int offsetOuterY;
    private final int offsetOuterZ;

    private final int offsetInnerX;
    private final int offsetInnerY;
    private final int offsetInnerZ;

    private final int outerRadius;
    private final int innerRadius;
    
    private final boolean inverted;


    public BotExcavatePatternAttributes(int x1, int y1, int z1, int outerRadius, int x2, int y2, int z2, int innerRadius, boolean inverted) {
        this.offsetOuterX = x1;
        this.offsetOuterY = y1;
        this.offsetOuterZ = z1;
        this.outerRadius = outerRadius;
        this.offsetInnerX = x2;
        this.offsetInnerY = y2;
        this.offsetInnerZ = z2;
        this.innerRadius = innerRadius;
        this.inverted = inverted;
    }


    public int getOffsetOuterX() {
        return offsetOuterX;
    }


    public int getOffsetOuterY() {
        return offsetOuterY;
    }


    public int getOffsetOuterZ() {
        return offsetOuterZ;
    }


    public int getOffsetInnerX() {
        return offsetInnerX;
    }


    public int getOffsetInnerY() {
        return offsetInnerY;
    }


    public int getOffsetInnerZ() {
        return offsetInnerZ;
    }


    public int getOuterRadius() {
        return outerRadius;
    }


    public int getInnerRadius() {
        return innerRadius;
    }


    public boolean isInverted() {
        return inverted;
    }


}