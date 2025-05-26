package com.devone.bot.core.brain.memory;

public class BotMemoryItem {
    public enum ItemKey {
        TOTAL("total"),
        SCAN_RADIUS("scan_radius"),
        TELEPORTED("teleports"),
        POSITION("position"),
        YAW("yaw"),
        TARGET("target"),
        TARGETS("targets"),
        SUGGESTION("suggestion"),        
        SUGGESTED_TARGET("suggested_target"),
        CALCULATED("calculated"),
        CONFIRMED("confirmed"),
        STUCKS("stucks"),
        REACHABLE("reachable"),
        WALKABLE("walkable");

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
