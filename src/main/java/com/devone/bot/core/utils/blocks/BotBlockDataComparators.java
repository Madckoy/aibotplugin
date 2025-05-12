package com.devone.bot.core.utils.blocks;

import java.util.Comparator;

public class BotBlockDataComparators {

    public static final Comparator<BotBlockData> X_ASC = Comparator.comparingInt(BotBlockData::getX);
    public static final Comparator<BotBlockData> X_DESC = X_ASC.reversed();

    public static final Comparator<BotBlockData> Y_ASC = Comparator.comparingInt(BotBlockData::getY);
    public static final Comparator<BotBlockData> Y_DESC = Y_ASC.reversed();

    public static final Comparator<BotBlockData> Z_ASC = Comparator.comparingInt(BotBlockData::getZ);
    public static final Comparator<BotBlockData> Z_DESC = Z_ASC.reversed();

    /**
     * Возвращает компаратор блоков по направлению оси.
     * UP → Y+, DOWN → Y-, EAST → X+, WEST → X-, SOUTH → Z+, NORTH → Z-
     */
    public static Comparator<BotBlockData> byAxisDirection(BotAxisDirection.AxisDirection dir) {
        return switch (dir) {
            case UP    -> Y_ASC;
            case DOWN  -> Y_DESC;
            case EAST  -> X_ASC;
            case WEST  -> X_DESC;
            case SOUTH -> Z_ASC;
            case NORTH -> Z_DESC;
            default    -> throw new IllegalArgumentException("Unknown axis direction: " + dir);
        };
    }
}
