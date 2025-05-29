package com.devone.bot.core.brain.cortex;

public class BotReactionResult {
        public final String name;
        public final Runnable action;

        public BotReactionResult(String name, Runnable action) {
            this.name = name;
            this.action = action;
        }
    }