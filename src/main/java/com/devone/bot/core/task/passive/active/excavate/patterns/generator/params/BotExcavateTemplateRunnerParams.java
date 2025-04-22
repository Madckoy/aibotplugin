package com.devone.bot.core.task.passive.active.excavate.patterns.generator.params;

public class BotExcavateTemplateRunnerParams {
    public int observerX, observerY, observerZ;

    public int offsetOuterX;
    public int offsetOuterY;
    public int offsetOuterZ;

    public int offsetInnerX;
    public int offsetInnerY;
    public int offsetInnerZ;

    public int outerRadius;
    public int innerRadius;

    public boolean inverted;

    public BotExcavateTemplateRunnerParams() {
        
        this.observerX = 0;
        this.observerY = 0;
        this.observerZ = 0;
        
        this.offsetOuterX = 0;
        this.offsetOuterY = 0;
        this.offsetOuterZ = 0;

        this.offsetInnerX = 0;
        this.offsetInnerY = 0;
        this.offsetInnerZ = 0;

        this.outerRadius = 1;
        this.innerRadius = 1;

        this.inverted    = true;
    }

    public BotExcavateTemplateRunnerParams(int observerX, int observerY, int observerZ,
                                           int offsetOuterX, int offsetOuterY, int offsetOuterZ, int outerRadius, 
                                           int offsetInnerX, int offsetInnerY, int offsetInnerZ, int innerRadius, 
                                           boolean inverted) {
        this.observerX = observerX;
        this.observerY = observerY;
        this.observerZ = observerZ;
        
        this.offsetOuterX = offsetOuterX;
        this.offsetOuterY = offsetOuterY;
        this.offsetOuterZ = offsetOuterZ;

        this.offsetInnerX = offsetInnerX;
        this.offsetInnerY = offsetInnerY;
        this.offsetInnerZ = offsetInnerZ;

        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;

        this.inverted    = inverted;

    }

    @Override
    public String toString() {
    return String.format(
        "📦 origin: (%d, %d, %d), offsetOuter: (%d, %d, %d), outerRadius: %d, offsetInner: (%d, %d, %d), innerRadius: %d, %s",
        observerX, 
        observerY, 
        observerZ,  
        offsetOuterX, 
        offsetOuterY, 
        offsetOuterZ, 
        outerRadius, 
        offsetInnerX, 
        offsetInnerY, 
        offsetInnerZ, 
        innerRadius, 
        inverted );
}
}
