package com.devone.bot.core.logic.task.brain.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;

public class BotBrainTaskParams extends BotTaskParams {

    private boolean allowPickupItems = true;
    //
    private boolean killAggressives = true;
    // 
    private boolean killPassives = true;
    //
    private int     unstuckStrategy = 1;
    /*
    0 - do nothing
    1 - excavate
    2 - teleport
      2.1 - teleport to random reachable spot, if available
      2.2 - teleport to random navigable spot, if available
      2.3 - teleport to random walkable spot,  if available
      2.4 - teleport to specific location
      2.5 - teleport to world spawn
      2.6 - teleport to nearest animal
    */
    private boolean allowViolence   = true;
    //
    private boolean allowTeleport   = true;
    //
    private boolean allowBroadcast  = true;
    //
    private boolean allowExcavation  = true;
    private boolean allowExploration = true;
    //

    public BotBrainTaskParams() {
        super();
        setIcon("🧠");
        setObjective("Think");
    }

    public static BotBrainTaskParams clone(BotBrainTaskParams source) {
        BotBrainTaskParams target = new BotBrainTaskParams();
        return target;
    }

    public boolean isAllowPickupItems() {
        return allowPickupItems;
    }

    public boolean isKillAggressives() {
        return killAggressives;
    }

    public boolean isKillPassives() {
        return killPassives;
    }

    public int getUnstuckStrategy() {
        return unstuckStrategy;
    }

    public boolean isAllowViolence() {
        return allowViolence;
    }

    public boolean isAllowTeleport() {
        return allowTeleport;
    }

    public boolean isAllowBroadcast() {
        return allowBroadcast;
    }

    public boolean isAllowExcavation() {
        return allowExcavation;
    }

    public boolean isAllowExploration() {
        return allowExploration;
    }

    public void setAllowPickupItems(boolean allowPickupItems) {
        this.allowPickupItems = allowPickupItems;
    }

    public void setKillAggressives(boolean killAggressives) {
        this.killAggressives = killAggressives;
    }

    public void setKillPassives(boolean killPassives) {
        this.killPassives = killPassives;
    }

    public void setUnstuckStrategy(int unstuckStrategy) {
        this.unstuckStrategy = unstuckStrategy;
    }

    public void setAllowViolence(boolean allowViolence) {
        this.allowViolence = allowViolence;
    }

    public void setAllowTeleport(boolean allowTeleport) {
        this.allowTeleport = allowTeleport;
    }

    public void setAllowBroadcast(boolean allowBroadcast) {
        this.allowBroadcast = allowBroadcast;
    }

    public void setAllowExcavation(boolean allowExcavation) {
        this.allowExcavation = allowExcavation;
    }

    public void setAllowExploration(boolean allowExploration) {
        this.allowExploration = allowExploration;
    }


    
}
