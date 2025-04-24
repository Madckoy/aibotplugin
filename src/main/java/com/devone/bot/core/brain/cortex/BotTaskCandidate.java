package com.devone.bot.core.brain.cortex;

import java.util.function.*;

public class BotTaskCandidate {
    private final DoubleSupplier weightSupplier;
    private final Supplier<Runnable> taskSupplier;
    private final BooleanSupplier condition;

    public BotTaskCandidate(DoubleSupplier weightSupplier, Supplier<Runnable> taskSupplier, BooleanSupplier condition) {
        this.weightSupplier = weightSupplier;
        this.taskSupplier = taskSupplier;
        this.condition = condition;
    }

    public double getWeight() {
        return weightSupplier.getAsDouble();
    }

    public boolean isAvailable() {
        return condition.getAsBoolean();
    }

    public Runnable getTask() {
        return taskSupplier.get();
    }
}
