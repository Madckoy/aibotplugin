package com.devone.bot.core.bot.brain.logic.navigation;

public class BotNavigationSummaryItem {
    private String id;
    private int calculated;
    private int confirmed;

    public BotNavigationSummaryItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCalculated() {
        return calculated;
    }

    public void setCalculated(int calculated) {
        this.calculated = calculated;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

}
