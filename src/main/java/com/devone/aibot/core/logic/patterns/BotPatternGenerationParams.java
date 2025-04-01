package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.utils.BotAxisDirection.AxisDirection;

public class BotPatternGenerationParams {
    public final int observerX, observerY, observerZ;
    public final int offsetX, offsetY, offsetZ;
    public final int outerRadius;
    public final int innerRadius;
    public final AxisDirection breakDirection;

    public BotPatternGenerationParams(int observerX, int observerY, int observerZ,
                                      int offsetX, int offsetY, int offsetZ,
                                      int outerRadius, int innerRadius,
                                      AxisDirection breakDirection) {
        this.observerX = observerX;
        this.observerY = observerY;
        this.observerZ = observerZ;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.breakDirection = breakDirection;
    }

    public int getCenterX() { return observerX + offsetX; }
    public int getCenterY() { return observerY + offsetY; }
    public int getCenterZ() { return observerZ + offsetZ; }
}
