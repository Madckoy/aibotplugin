package com.devone.bot.core.utils.blocks;

import java.util.Comparator;

public class BotBlockDataComparators {

    public static final Comparator<BotBlockData> X_ASC = new Comparator<>() {
        @Override public int compare(BotBlockData a, BotBlockData b) {
            return Integer.compare(a.getX(), b.getX());
        }
    };

    public static final Comparator<BotBlockData> X_DESC = new Comparator<>() {
        @Override public int compare(BotBlockData a, BotBlockData b) {
            return Integer.compare(b.getX(), a.getX());
        }
    };

    public static final Comparator<BotBlockData> Y_ASC = new Comparator<>() {
        @Override public int compare(BotBlockData a, BotBlockData b) {
            return Integer.compare(a.getY(), b.getY());
        }
    };

    public static final Comparator<BotBlockData> Y_DESC = new Comparator<>() {
        @Override public int compare(BotBlockData a, BotBlockData b) {
            return Integer.compare(b.getY(), a.getY());
        }
    };

    public static final Comparator<BotBlockData> Z_ASC = new Comparator<>() {
        @Override public int compare(BotBlockData a, BotBlockData b) {
            return Integer.compare(a.getZ(), b.getZ());
        }
    };

    public static final Comparator<BotBlockData> Z_DESC = new Comparator<>() {
        @Override public int compare(BotBlockData a, BotBlockData b) {
            return Integer.compare(b.getZ(), a.getZ());
        }
    };

    public static Comparator<BotBlockData> byAxisDirection(BotAxisDirection.AxisDirection dir) {
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
