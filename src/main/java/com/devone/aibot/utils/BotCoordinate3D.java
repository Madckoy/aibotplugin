package com.devone.aibot.utils;

import java.util.Objects;

public class BotCoordinate3D {
    public int x, y, z;

    public BotCoordinate3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotCoordinate3D)) return false;
        BotCoordinate3D that = (BotCoordinate3D) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
 
    @Override
    public String toString() {
        return String.format("(%d,%d,%d)", x, y, z);
    }
}
