package com.devone.bot.core.utils.blocks;

public class BotPositionSight extends BotPosition {
    private float yaw;
    private float pitch;

    public BotPositionSight() {
        super();
    }

    public BotPositionSight(double x, double y, double z, float yaw, float pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public BotPositionSight(BotPosition base, float yaw, float pitch) {
        super(base.getX(), base.getY(), base.getZ());
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public BotPositionKey toKey() {
        return super.toKey();
    }

    @Override
    public String toString() {
        return String.format(
            "BotPositionSight[%.2f, %.2f, %.2f | Yaw: %.1f°, Pitch: %.1f° → Key=%s]",
            getX(), getY(), getZ(), yaw, pitch, toKey()
        );
    }
}
