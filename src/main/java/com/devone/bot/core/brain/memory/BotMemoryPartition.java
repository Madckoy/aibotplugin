package com.devone.bot.core.brain.memory;

public class BotMemoryPartition {
    public enum PartitionKey {
        STATS("Stats"),
        KILLED("Killed"),
        DESTROYED("Destroyed"),
        NAVIGATION("Navigation"),
        CANDIDATES("Candidates"),
        SUMMARY("Summary"),
        VISITED("Visited");

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
