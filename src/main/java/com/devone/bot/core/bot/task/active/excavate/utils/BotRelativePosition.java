package com.devone.bot.core.bot.task.active.excavate.utils;

public class BotRelativePosition {

    public enum Horizontal {
        CENTER, NORTH, SOUTH, EAST, WEST
    }

    public enum Vertical {
        CENTER, ABOVE, BELOW
    }

    private Horizontal horizontal;
    private Vertical vertical;

    private int deltaX;
    private int deltaY;
    private int deltaZ;

    public BotRelativePosition(Horizontal horizontal, Vertical vertical) {
        this(horizontal, vertical, 0, 0, 0);
    }

    public BotRelativePosition(Horizontal horizontal, Vertical vertical, int deltaX, int deltaY, int deltaZ) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
    }

    public int[] toOffset(int radius) {
        int offsetX = switch (horizontal) {
            case EAST -> radius;
            case WEST -> -radius;
            default -> 0;
        };

        int offsetZ = switch (horizontal) {
            case SOUTH -> radius;
            case NORTH -> -radius;
            default -> 0;
        };

        int offsetY = switch (vertical) {
            case ABOVE -> radius;
            case BELOW -> -radius;
            default -> 0;
        };

        return new int[] {
            offsetX + deltaX,
            offsetY + deltaY,
            offsetZ + deltaZ
        };
    }

    public Horizontal getHorizontal() {
        return horizontal;
    }

    public Vertical getVertical() {
        return vertical;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public int getDeltaZ() {
        return deltaZ;
    }

    @Override
    public String toString() {
        return String.format("RelativeBotPosition[%s-%s, Δx=%d, Δy=%d, Δz=%d]",
                vertical, horizontal, deltaX, deltaY, deltaZ);
    }
}