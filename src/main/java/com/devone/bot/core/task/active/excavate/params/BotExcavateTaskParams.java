
package com.devone.bot.core.task.active.excavate.params;

import java.util.Set;

import org.bukkit.Material;

import com.devone.bot.core.task.passive.params.BotTaskParams;
import com.devone.bot.core.utils.BotConstants;

public class BotExcavateTaskParams extends BotTaskParams {

    private Set<Material> targetMaterials;

    private int maxBlocks    = 64;

    private boolean pickup   = true;

    private String patternName = BotConstants.DEFAULT_PATTERN_BREAK;

    public BotExcavateTaskParams() {
        super();
        setIcon("🧊");
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
                ", maxBlocks="     + maxBlocks +
                ", patternName='"  + patternName + "}";
    }
}
