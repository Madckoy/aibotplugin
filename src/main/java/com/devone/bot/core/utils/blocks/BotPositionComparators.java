package com.devone.bot.core.utils.blocks;

import java.util.Comparator;

public class BotPositionComparators {

    public static final Comparator<BotPosition> X_ASC = Comparator.comparingDouble(BotPosition::getX);
    public static final Comparator<BotPosition> X_DESC = X_ASC.reversed();

    public static final Comparator<BotPosition> Y_ASC = Comparator.comparingDouble(BotPosition::getY);
    public static final Comparator<BotPosition> Y_DESC = Y_ASC.reversed();

    public static final Comparator<BotPosition> Z_ASC = Comparator.comparingDouble(BotPosition::getZ);
    public static final Comparator<BotPosition> Z_DESC = Z_ASC.reversed();

    /**
     * Возвращает компаратор позиции по направлению.
     * UP — Y+, DOWN — Y-, EAST — X+, WEST — X-, SOUTH — Z+, NORTH — Z-
     */
    public static Comparator<BotPosition> byAxisDirection(BotAxisDirection.AxisDirection dir) {
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
