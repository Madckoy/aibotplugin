package com.devone.bot.core.brain.memory;

public class BotMemoryPartition {
    public enum PartitionKey {
        STATS("stats"),
        KILLED("kills"),
        DESTROYED("breaks"),
        NAVIGATION("navigation"),
        CANDIDATES("candidates"),
        SUMMARY("summary"),
        VISITED("visited");

        private final String value;

        PartitionKey(String value) {
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
