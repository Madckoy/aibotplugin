package com.devone.bot.core.logic.tasks.destruction.params;

import java.util.Set;

import org.bukkit.Material;

import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.utils.BotAxisDirection.AxisDirection;

public class BotBreakTaskParams extends BotTaskParams{
    public Set<Material> targetMaterials;
    public int maxBlocks;
    public int outerRadius;
    public int innerRadius;
    public boolean shouldPickup;
    public boolean destroyAllIfNoTarget;
    public AxisDirection breakDirection;
    public int offsetX;
    public int offsetY;
    public int offsetZ;
    public String patternName;
    
    public BotBreakTaskParams() {
        this.targetMaterials = null;
        this.maxBlocks = 0;
        this.outerRadius = 0;
        this.innerRadius = 0;
        this.shouldPickup = false;
        this.destroyAllIfNoTarget = false;
        this.breakDirection = AxisDirection.DOWN;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.patternName = null;
    }
    public BotBreakTaskParams(Set<Material> targetMaterials, int maxBlocks, int outerRadius, int innerRadius,
            boolean shouldPickup, boolean destroyAllIfNoTarget, AxisDirection breakDirection, int offsetX, int offsetY,
            int offsetZ, String patternName) {
        this.targetMaterials = targetMaterials;
        this.maxBlocks = maxBlocks;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.shouldPickup = shouldPickup;
        this.destroyAllIfNoTarget = destroyAllIfNoTarget;
        this.breakDirection = breakDirection;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.patternName = patternName;
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
    public boolean isDestroyAllIfNoTarget() {
        return destroyAllIfNoTarget;
    }
    public void setDestroyAllIfNoTarget(boolean destroyAllIfNoTarget) {
        this.destroyAllIfNoTarget = destroyAllIfNoTarget;
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
        return "BotBreakTaskParams{" +
                "targetMaterials=" + targetMaterials +
                ", maxBlocks=" + maxBlocks +
                ", outerRadius=" + outerRadius +
                ", innerRadius=" + innerRadius +
                ", shouldPickup=" + shouldPickup +
                ", destroyAllIfNoTarget=" + destroyAllIfNoTarget +
                ", breakDirection=" + breakDirection +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", offsetZ=" + offsetZ +
                ", patternName='" + patternName + '\'' +
                '}';
    }
}
