
package com.devone.bot.core.logic.task.excavate.params;

import java.util.Set;

import org.bukkit.Material;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;

public class BotExcavateTaskParams extends BotTaskParams {
    private Set<Material> targetMaterials;
    private int maxBlocks = 64;
    private int outerRadius = BotConstants.DEFAULT_OUTER_RADIUS;
    private int innerRadius = BotConstants.DEFAULT_INNER_RADIUS;
    private boolean shouldPickup = true;
    private AxisDirection breakDirection = AxisDirection.DOWN;
    private int offsetX = 0;
    private int offsetY = 0;
    private int offsetZ = 0;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;

    public BotExcavateTaskParams() {
        super();
        setIcon("🪨");
        setObjective("Excavate");
    }

    public Set<Material> getTargetMaterials() {
        return targetMaterials;
    }

    public void setTargetMaterials(Set<Material> targetMaterials) {
        this.targetMaterials = targetMaterials;
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

    public int getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(int outerRadius) {
        this.outerRadius = outerRadius;
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(int innerRadius) {
        this.innerRadius = innerRadius;
    }

    public boolean isShouldPickup() {
        return shouldPickup;
    }

    public void setShouldPickup(boolean shouldPickup) {
        this.shouldPickup = shouldPickup;
    }

    public AxisDirection getBreakDirection() {
        return breakDirection;
    }

    public void setBreakDirection(AxisDirection breakDirection) {
        this.breakDirection = breakDirection;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(int offsetZ) {
        this.offsetZ = offsetZ;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    @Override
    public String toString() {
        return "BotExcavateTaskParams{" +
                "targetMaterials=" + targetMaterials +
                ", maxBlocks=" + maxBlocks +
                ", outerRadius=" + outerRadius +
                ", innerRadius=" + innerRadius +
                ", shouldPickup=" + shouldPickup +
                ", breakDirection=" + breakDirection +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", offsetZ=" + offsetZ +
                ", patternName='" + patternName + "}";
    }
}
