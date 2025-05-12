package com.devone.bot.core.utils.blocks;

public class BotPosition {
    private double x, y, z;

    public BotPosition() {
        this(0, 0, 0);
    }

    public BotPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BotPosition(BotPosition other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    // Основные координаты — округлённые в меньшую сторону (поведение по умолчанию)
    public int getX() {
        return (int) Math.floor(x);
    }

    public int getY() {
        return (int) Math.floor(y);
    }

    public int getZ() {
        return (int) Math.floor(z);
    }

    // Точные координаты (если нужно)
    public double getExactX() {
        return x;
    }

    public double getExactY() {
        return y;
    }

    public double getExactZ() {
        return z;
    }

    // Сеттеры сохраняют точность
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    // Упрощённый доступ к ключу и блоку
    public BotPositionKey toKey() {
        return new BotPositionKey(getX(), getY(), getZ());
    }

    public BotBlockData toBlockData() {
        return new BotBlockData(getX(), getY(), getZ());
    }

    // Расстояние до другой позиции (по double)
    public double distanceTo(BotPosition other) {
        if (other == null) return Double.MAX_VALUE;
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // Расстояние до блока
    public int blockDistanceTo(BotBlockData other) {
        if (other == null) return Integer.MAX_VALUE;
        int dx = getX() - other.getX();
        int dy = getY() - other.getY();
        int dz = getZ() - other.getZ();
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // Расстояние до другой позиции (целочисленно)
    public int blockDistanceTo(BotPosition other) {
        if (other == null) return Integer.MAX_VALUE;
        int dx = getX() - other.getX();
        int dy = getY() - other.getY();
        int dz = getZ() - other.getZ();
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("%.2f, %.2f, %.2f", x, y, z);
    }

    public String toCompactString() {
        return String.format("%d, %d, %d", getX(), getY(), getZ());
    }
 
    public double distanceTo(BotBlockData other) {
    if (other == null) return Double.MAX_VALUE;
    return distanceTo(other.getPosition());
}
}
