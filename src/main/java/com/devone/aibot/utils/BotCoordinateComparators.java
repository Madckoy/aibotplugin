package com.devone.aibot.utils;

import java.util.Comparator;

public class BotCoordinateComparators {

    public static final Comparator<Bot3DCoordinate> X_ASC = new Comparator<>() {
        @Override public int compare(Bot3DCoordinate a, Bot3DCoordinate b) {
            return Integer.compare(a.x, b.x);
        }
    };

    public static final Comparator<Bot3DCoordinate> X_DESC = new Comparator<>() {
        @Override public int compare(Bot3DCoordinate a, Bot3DCoordinate b) {
            return Integer.compare(b.x, a.x);
        }
    };

    public static final Comparator<Bot3DCoordinate> Y_ASC = new Comparator<>() {
        @Override public int compare(Bot3DCoordinate a, Bot3DCoordinate b) {
            return Integer.compare(a.y, b.y);
        }
    };

    public static final Comparator<Bot3DCoordinate> Y_DESC = new Comparator<>() {
        @Override public int compare(Bot3DCoordinate a, Bot3DCoordinate b) {
            return Integer.compare(b.y, a.y);
        }
    };

    public static final Comparator<Bot3DCoordinate> Z_ASC = new Comparator<>() {
        @Override public int compare(Bot3DCoordinate a, Bot3DCoordinate b) {
            return Integer.compare(a.z, b.z);
        }
    };

    public static final Comparator<Bot3DCoordinate> Z_DESC = new Comparator<>() {
        @Override public int compare(Bot3DCoordinate a, Bot3DCoordinate b) {
            return Integer.compare(b.z, a.z);
        }
    };

    public static Comparator<Bot3DCoordinate> byAxisDirection(BotAxisDirection.AxisDirection dir) {
        switch (dir) {
            case UP:    return Y_ASC;
            case DOWN:  return Y_DESC;
            case EAST:  return X_ASC;
            case WEST:  return X_DESC;
            case SOUTH: return Z_ASC;
            case NORTH: return Z_DESC;
            default:    return null;
        }
    }
}
