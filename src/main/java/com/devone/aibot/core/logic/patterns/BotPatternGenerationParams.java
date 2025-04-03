package com.devone.aibot.core.logic.patterns;

public class BotPatternGenerationParams {
    public final int observerX, observerY, observerZ;
    public final int offsetX, offsetY, offsetZ;
    public final int outerRadius;
    public final int innerRadius;

    public BotPatternGenerationParams(int observerX, int observerY, int observerZ,
                                      int offsetX, int offsetY, int offsetZ,
                                      int outerRadius, int innerRadius) {
        this.observerX = observerX;
        this.observerY = observerY;
        this.observerZ = observerZ;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;

    }

    @Override
    public String toString() {
    return String.format(
        "📦 origin: (%d, %d, %d), offset: (%d, %d, %d), outerRadius: %d, innerRadius: %d",
        observerX, observerY, observerZ,
        offsetX, offsetY, offsetZ,
        outerRadius, innerRadius
    );
}
}
