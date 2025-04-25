package com.devone.bot.core.task.active.excavate.patterns.generator.params;

public class BotExcavateTemplateRunnerParams {
    public double observerX, observerY, observerZ;

    public double offsetOuterX;
    public double offsetOuterY;
    public double offsetOuterZ;

    public double offsetInnerX;
    public double offsetInnerY;
    public double offsetInnerZ;

    public double outerRadius;
    public double innerRadius;

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

    public BotExcavateTemplateRunnerParams(double observerX, double observerY, double observerZ,
                                           double offsetOuterX, double offsetOuterY, double offsetOuterZ, double outerRadius, 
                                           double offsetInnerX, double offsetInnerY, double offsetInnerZ, double innerRadius, 
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
        "📦 origin: (%1$,.2f, %2$,.2f, %3$,.2f), offsetOuter: (%4$,.2f, %5$,.2f, %6$,.2f), outerRadius: %7$,.2f, offsetInner: (%8$,.2f, %9$,.2f, %10$,.2f), innerRadius: %11$,.2f, %s",
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
