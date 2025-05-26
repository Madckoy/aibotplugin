package com.devone.bot.core.brain.memory;

public class BotMemoryItem {
    public enum ItemKey {
        TOTAL("Total"),
        SCAN_RADIUS("ScanRadius"),
        TELEPORTED("Teleported"),
        POSITION("Position"),
        YAW("Yaw"),
        TARGET("Target"),
        SUGGESTION("Suggestion"),        
        SUGGESTED_TARGET("SuggestedTarget"),
        CALCULATED("Calculated"),
        CONFIRMED("Confirmed"),
        STUCKS("Stucks");

        private final String value;

        ItemKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value; // ← чтобы можно было просто вставить как строку
        }
    }
}
