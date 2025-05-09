package com.devone.bot.core.task.active.brain.params;

import com.devone.bot.core.task.passive.params.BotTaskParams;

public class BotBrainTaskParams extends BotTaskParams {

    private boolean killAggressives = true;
    // 
    private boolean killPassives = true;
    //
    private int     unstuckStrategy = 1;
    /*
    0 - Swimm
    1 - Explore
    2 - Excavate
    3 - Teleport
      3.1 - teleport to random reachable spot, if available
      3.2 - teleport to random navigable spot, if available
      3.3 - teleport to random walkable spot,  if available
      3.4 - teleport to specific location
      3.5 - teleport to world spawn
      3.6 - teleport to nearest animal
    */
    private boolean allowViolence   = true;
    //
    private boolean allowTeleport   = true;
    //
    private boolean allowBroadcast  = true;
    //
    private boolean allowExcavation  = true;
    private boolean allowExploration = true;
    private boolean allowSwimming    = true;
    //
    private long memoryExpirationMillis = 30 * 60 * 1000; // по умолчанию 30 минут
    //
    private double explorationWeight = 0.7;
    private double excavationWeight = 0.3;
    private double violenceWeight   = 1;
    //


    public BotBrainTaskParams() {
        super();
        setIcon("🧠");
        setObjective("Think");
    }

    public static BotBrainTaskParams clone(BotBrainTaskParams source) {
        if (source == null) return new BotBrainTaskParams();
    
        BotBrainTaskParams target = new BotBrainTaskParams();
        target.setAllowExploration(source.isAllowExploration());
        target.setAllowSwimming(source.isAllowSwimming());
        target.setAllowExcavation(source.isAllowExcavation());
        target.setAllowTeleport(source.isAllowTeleport());
        target.setAllowBroadcast(source.isAllowBroadcast());
        target.setAllowViolence(source.isAllowViolence());
        target.setKillAggressives(source.isKillAggressives());
        target.setKillPassives(source.isKillPassives());
        target.setUnstuckStrategy(source.getUnstuckStrategy());
        target.setExplorationWeight(source.getExplorationWeight());
        target.setExcavationWeight(source.getExcavationWeight());
        target.setViolenceWeight(source.getViolenceWeight());
        return target;
    }

    public boolean isAllowSwimming() {
        return allowSwimming;
    }

    public void setAllowSwimming(boolean allowSwimming) {
        this.allowSwimming = allowSwimming;
    }


    public void setViolenceWeight(double violenceWeight) {
        this.violenceWeight = violenceWeight;
    }

    public double getViolenceWeight() {
        return violenceWeight;
    }

    public double getExplorationWeight() {
        return explorationWeight;
    }

    public void setExplorationWeight(double explorationWeight) {
        this.explorationWeight = explorationWeight;
    }

    public void setExcavationWeight(double excavationWeight) {
        this.excavationWeight = excavationWeight;
    }

    public double getExcavationWeight() {
        return excavationWeight;
    }

    public long getMemoryExpirationMillis() {
        return memoryExpirationMillis;
    }
    
    public void setMemoryExpirationMillis(long memoryExpirationMillis) {
        this.memoryExpirationMillis = memoryExpirationMillis;
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
