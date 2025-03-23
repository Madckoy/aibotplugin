package com.devone.aibot.core.logic.patterns.destruction;

import com.devone.aibot.core.Bot;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Абстрактная реализация базового паттерна разрушения,
 * предоставляющая базовую реализацию конфигурации.
 */
public abstract class AbstractBotBreakPattern implements IBotBreakPattern {

    public boolean initialized = false;

    protected int radius = 0; // Значение по умолчанию, может быть переопределено

    public final Queue<Location> blocksToBreak = new LinkedList<>();

    @Override
    public IBotBreakPattern configure(int radius) {
        setRadius(radius) ;
        return this;
    }

    public void setRadius(int rds) {
        radius = rds;
    }

    @Override
    public abstract Location findNextBlock(Bot bot, Map<Location, ?> geoMap);

    @Override
    public abstract boolean isFinished();

    @Override
    public abstract String getName();
}
