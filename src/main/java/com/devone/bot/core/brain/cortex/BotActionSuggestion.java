package com.devone.bot.core.brain.cortex;

public class BotActionSuggestion {

    public static enum Suggestion {
        NONE,
        NAVIGATION_CALCULATE,
        NAVIGATION_SIMULATE,
        NAVIGATION_MOVE,
        NAVIGATION_CHANGE_DIRECTION,
        NAVIGATION_TELEPORT
    }
}
