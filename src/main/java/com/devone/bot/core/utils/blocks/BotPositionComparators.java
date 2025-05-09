package com.devone.bot.core.utils.blocks;

import java.util.Comparator;

public class BotPositionComparators {

    public static final Comparator<BotPosition> X_ASC = new Comparator<>() {
        @Override public int compare(BotPosition a, BotPosition b) {
            return Double.compare(a.getX(), b.getX());
        }
    };

    public static final Comparator<BotPosition> X_DESC = new Comparator<>() {
        @Override public int compare(BotPosition a, BotPosition b) {
            return Double.compare(b.getX(), a.getX());
        }
    };

    public static final Comparator<BotPosition> Y_ASC = new Comparator<>() {
        @Override public int compare(BotPosition a, BotPosition b) {
            return Double.compare(a.getY(), b.getY());
        }
    };

    public static final Comparator<BotPosition> Y_DESC = new Comparator<>() {
        @Override public int compare(BotPosition a, BotPosition b) {
            return Double.compare(b.getY(), a.getY());
        }
    };

    public static final Comparator<BotPosition> Z_ASC = new Comparator<>() {
        @Override public int compare(BotPosition a, BotPosition b) {
            return Double.compare(a.getZ(), b.getZ());
        }
    };

    public static final Comparator<BotPosition> Z_DESC = new Comparator<>() {
        @Override public int compare(BotPosition a, BotPosition b) {
            return Double.compare(b.getZ(), a.getZ());
        }
    };

    public static Comparator<BotPosition> byAxisDirection(BotAxisDirection.AxisDirection dir) {
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
