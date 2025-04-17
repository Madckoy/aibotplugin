package com.devone.bot.utils.blocks;

import java.util.Comparator;

public class BotLocationComparators {

    public static final Comparator<BotLocation> X_ASC = new Comparator<>() {
        @Override public int compare(BotLocation a, BotLocation b) {
            return Integer.compare(a.getX(), b.getX());
        }
    };

    public static final Comparator<BotLocation> X_DESC = new Comparator<>() {
        @Override public int compare(BotLocation a, BotLocation b) {
            return Integer.compare(b.getX(), a.getX());
        }
    };

    public static final Comparator<BotLocation> Y_ASC = new Comparator<>() {
        @Override public int compare(BotLocation a, BotLocation b) {
            return Integer.compare(a.getY(), b.getY());
        }
    };

    public static final Comparator<BotLocation> Y_DESC = new Comparator<>() {
        @Override public int compare(BotLocation a, BotLocation b) {
            return Integer.compare(b.getY(), a.getY());
        }
    };

    public static final Comparator<BotLocation> Z_ASC = new Comparator<>() {
        @Override public int compare(BotLocation a, BotLocation b) {
            return Integer.compare(a.getZ(), b.getZ());
        }
    };

    public static final Comparator<BotLocation> Z_DESC = new Comparator<>() {
        @Override public int compare(BotLocation a, BotLocation b) {
            return Integer.compare(b.getZ(), a.getZ());
        }
    };

    public static Comparator<BotLocation> byAxisDirection(BotAxisDirection.AxisDirection dir) {
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
