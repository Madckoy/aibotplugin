package com.devone.bot.core.bot.task.active.excavate.patterns.generator.params;

public class BotExcavatePatternGenerationParams {
    public final int observerX, observerY, observerZ;

    public final int offsetOuterX;
    public final int offsetOuterY;
    public final int offsetOuterZ;

    public final int offsetInnerX;
    public final int offsetInnerY;
    public final int offsetInnerZ;


    public final int outerRadius;
    public final int innerRadius;

    public final boolean inverted;

    public BotExcavatePatternGenerationParams(int observerX, int observerY, int observerZ,
                                      int offsetOuterX, int offsetOuterY, int offsetOuterZ, int outerRadius, 
                                      int offsetInnerX, int offsetInnerY, int offsetInnerZ,
                                      int innerRadius, boolean inverted) {
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
