package com.devone.bot.core.utils.blocks;

import java.util.Objects;

public class BotPositionKey {
    private final int x, y, z;

    public BotPositionKey(double x, double y, double z) {
        this.x = (int)Math.round(x);
        this.y = (int)Math.round(y);
        this.z = (int)Math.round(z);
    }

    public BotPositionKey(BotPosition pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotPositionKey)) return false;
        BotPositionKey that = (BotPositionKey) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
