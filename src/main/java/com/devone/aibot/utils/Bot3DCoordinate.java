package com.devone.aibot.utils;

import java.util.Objects;

public class Bot3DCoordinate {
    public final int x, y, z;

    public Bot3DCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bot3DCoordinate)) return false;
        Bot3DCoordinate that = (Bot3DCoordinate) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
