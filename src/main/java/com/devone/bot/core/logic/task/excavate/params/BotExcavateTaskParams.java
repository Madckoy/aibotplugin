package com.devone.bot.core.logic.task.excavate.params;

import java.util.Set;

import org.bukkit.Material;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotAxisDirection.AxisDirection;

public class BotExcavateTaskParams extends BotTaskParams{
    private Set<Material> targetMaterials;
    private int maxBlocks  = 64;
    private int outerRadius =  BotConstants.DEFAULT_OUTER_RADIUS;
    private int innerRadius =  BotConstants.DEFAULT_INNER_RADIUS;
    private boolean shouldPickup = true;
    private boolean destroyAllIfNoTarget = false;
    private AxisDirection breakDirection = AxisDirection.DOWN;;
    private int offsetX = 0;
    private int offsetY = 0;
    private int offsetZ = 0;
    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;
    private String icon = "🪨";
    private String objective = "Excavate";

    public BotExcavateTaskParams() {
        super(BotExcavateTaskParams.class.getSimpleName());
        this.targetMaterials = null;
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    public BotExcavateTaskParams(String class_name) {
        super(class_name);
        this.targetMaterials = null;
        setIcon(icon);
        setObjective(objective);
        setDefaults();
    }

    public BotExcavateTaskParams(Set<Material> targetMaterials, int maxBlocks, int outerRadius, int innerRadius,
            boolean shouldPickup, boolean destroyAllIfNoTarget, AxisDirection breakDirection, int offsetX, int offsetY,
            int offsetZ, String patternName) {
        super(BotExcavateTaskParams.class.getSimpleName());       
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
        setIcon(icon);
        setObjective(objective);
        setDefaults();
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
    public Object setDefaults() {
        config.set("excavate.pattern",      this.patternName);
        config.set("excavate.outer.radius", this.outerRadius);
        config.set("excavate.inner.radius", this.innerRadius);
        config.set("excavate.offsetX", this.offsetX);
        config.set("excavate.offsetY", this.offsetY);
        config.set("excavate.offsetZ", this.offsetZ);

        super.setDefaults();
        return this;
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
