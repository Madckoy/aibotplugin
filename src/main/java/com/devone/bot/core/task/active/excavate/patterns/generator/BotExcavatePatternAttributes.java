package com.devone.bot.core.task.active.excavate.patterns.generator;

import com.devone.bot.core.utils.blocks.BotPosition;

public class BotExcavatePatternAttributes {
    
    private final BotPosition offsetOuter;
    private final BotPosition offsetInner;

    private final double outerRadius;
    private final double innerRadius;
    
    private final boolean inverted;


    public BotExcavatePatternAttributes(BotPosition offsetOuter, double outerRadius, BotPosition offsetInner, double innerRadius, boolean inverted) {
        this.offsetOuter = new BotPosition(offsetOuter);
        this.outerRadius = outerRadius;
        this.offsetInner = new BotPosition(offsetInner);
        this.innerRadius = innerRadius;
        this.inverted = inverted;
    }


    public BotPosition getOffsetOuter() {
        return offsetOuter;
    }

    public BotPosition getOffsetInner() {
        return offsetInner;
    }

    public double getOuterRadius() {
        return outerRadius;
    }


    public double getInnerRadius() {
        return innerRadius;
    }


    public boolean isInverted() {
        return inverted;
    }


}