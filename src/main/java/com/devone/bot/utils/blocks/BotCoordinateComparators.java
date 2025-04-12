package com.devone.bot.utils.blocks;

import java.util.Comparator;

public class BotCoordinateComparators {

    public static final Comparator<BotCoordinate3D> X_ASC = new Comparator<>() {
        @Override public int compare(BotCoordinate3D a, BotCoordinate3D b) {
            return Integer.compare(a.x, b.x);
        }
    };

    public static final Comparator<BotCoordinate3D> X_DESC = new Comparator<>() {
        @Override public int compare(BotCoordinate3D a, BotCoordinate3D b) {
            return Integer.compare(b.x, a.x);
        }
    };

    public static final Comparator<BotCoordinate3D> Y_ASC = new Comparator<>() {
        @Override public int compare(BotCoordinate3D a, BotCoordinate3D b) {
            return Integer.compare(a.y, b.y);
        }
    };

    public static final Comparator<BotCoordinate3D> Y_DESC = new Comparator<>() {
        @Override public int compare(BotCoordinate3D a, BotCoordinate3D b) {
            return Integer.compare(b.y, a.y);
        }
    };

    public static final Comparator<BotCoordinate3D> Z_ASC = new Comparator<>() {
        @Override public int compare(BotCoordinate3D a, BotCoordinate3D b) {
            return Integer.compare(a.z, b.z);
        }
    };

    public static final Comparator<BotCoordinate3D> Z_DESC = new Comparator<>() {
        @Override public int compare(BotCoordinate3D a, BotCoordinate3D b) {
            return Integer.compare(b.z, a.z);
        }
    };

    public static Comparator<BotCoordinate3D> byAxisDirection(BotAxisDirection.AxisDirection dir) {
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
