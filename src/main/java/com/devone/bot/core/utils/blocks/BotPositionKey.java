package com.devone.bot.core.utils.blocks;

import java.util.Objects;

public class BotPositionKey {
    private final int x, y, z;

    public BotPositionKey(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BotPositionKey(double x, double y, double z) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    public BotPositionKey(BotPosition pos) {
        this((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()), (int) Math.floor(pos.getZ()));
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

   // --- Distance ---
    public int distanceTo(BotPositionKey other) {
        if (other == null) return Integer.MAX_VALUE;
        int  dx = x - other.x;
        int  dy = y - other.y;
        int  dz = z - other.z;
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public int distanceTo(BotBlockData other) {
        if (other == null) return Integer.MAX_VALUE;
        return distanceTo(other.getPosition().getPositionKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }
}
