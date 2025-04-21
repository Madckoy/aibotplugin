
package com.devone.bot.core.bot.task.active.excavate.params;

import java.util.Set;

import org.bukkit.Material;

import com.devone.bot.core.bot.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotExcavateTaskParams extends BotTaskParams {
    private Set<Material> targetMaterials;
    private int maxBlocks    = 64;
    private int outerRadius  = BotConstants.DEFAULT_OUTER_RADIUS;
    private int innerRadius  = BotConstants.DEFAULT_INNER_RADIUS;
    private boolean pickup   = true;

    private boolean inverted = false;

    private int offsetOuterX = 0;
    private int offsetOuterY = 0;
    private int offsetOuterZ = 0;

    private int offsetInnerX = 0;
    private int offsetInnerY = 0;
    private int offsetInnerZ = 0;
    
    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public int getOffsetOuterX() {
        return offsetOuterX;
    }

    public void setOffsetOuterX(int offsetOuterX) {
        this.offsetOuterX = offsetOuterX;
    }

    public int getOffsetOuterY() {
        return offsetOuterY;
    }

    public void setOffsetOuterY(int offsetOuterY) {
        this.offsetOuterY = offsetOuterY;
    }

    public int getOffsetOuterZ() {
        return offsetOuterZ;
    }

    public void setOffsetOuterZ(int offsetOuterZ) {
        this.offsetOuterZ = offsetOuterZ;
    }

    public int getOffsetInnerX() {
        return offsetInnerX;
    }

    public void setOffsetInnerX(int offsetInnerX) {
        this.offsetInnerX = offsetInnerX;
    }

    public int getOffsetInnerY() {
        return offsetInnerY;
    }

    public void setOffsetInnerY(int offsetInnerY) {
        this.offsetInnerY = offsetInnerY;
    }

    public int getOffsetInnerZ() {
        return offsetInnerZ;
    }

    public void setOffsetInnerZ(int offsetInnerZ) {
        this.offsetInnerZ = offsetInnerZ;
    }

    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;

    public BotExcavateTaskParams() {
        super();
        setIcon("🪨");
        setObjective("Excavate");
    }

    public void setPickup(boolean pickup) {
        this.pickup = pickup;
    }

    public boolean isPickup() {
        return pickup;
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
                ", shouldPickup=" + pickup +
                ", offsetOuterX=" + offsetOuterX +
                ", offsetOuterY=" + offsetOuterY +
                ", offsetOuterZ=" + offsetOuterZ +
                ", offsetInnerX=" + offsetInnerX +
                ", offsetInnerY=" + offsetInnerY +
                ", offsetInnerZ=" + offsetInnerZ +
                ", inverted    =" + inverted + 
                ", patternName='" + patternName + "}";
    }
}
