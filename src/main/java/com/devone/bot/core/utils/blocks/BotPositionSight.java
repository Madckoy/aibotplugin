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

    public BotPositionSight copyWithYawPitch(float newYaw, float newPitch) {
        return new BotPositionSight(getX(), getY(), getZ(), newYaw, newPitch);
    }

    public String toCompactString() {
        return String.format("%d, %d, %d | %.0f°/%.0f°", getX(), getY(), getZ(), yaw, pitch);
    }

    @Override
    public String toString() {
        return String.format(
            "BotPositionSight[%.2f, %.2f, %.2f | Yaw: %.1f°, Pitch: %.1f° → Key=%s]",
            getExactX(), getExactY(), getExactZ(), yaw, pitch, toKey()
        );
    }

    /**
     * Проверяет, смотрит ли бот примерно в сторону указанного блока.
     */
    public boolean isLookingRoughlyAt(BotBlockData target, float angleToleranceDeg) {
        if (target == null) return false;

        float[] desired = calculateYawPitchTo(this, target);
        float desiredYaw = desired[0];
        float desiredPitch = desired[1];

        float yawDiff = Math.abs(wrapAngleTo180(this.yaw - desiredYaw));
        float pitchDiff = Math.abs(this.pitch - desiredPitch);

        return yawDiff <= angleToleranceDeg && pitchDiff <= angleToleranceDeg;
    }

    public boolean isLookingRoughlyAt(BotBlockData target) {
        return isLookingRoughlyAt(target, 15f);
    }

    /**
     * Вычисляет yaw/pitch для взгляда от позиции к цели (центру блока).
     */
    public static float[] calculateYawPitchTo(BotPosition from, BotBlockData to) {
        double dx = to.getX() + 0.5 - from.getX();
        double dy = to.getY() + 0.5 - from.getY();
        double dz = to.getZ() + 0.5 - from.getZ();

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, distanceXZ));

        yaw = wrapAngleTo180(yaw);
        return new float[]{yaw, pitch};
    }

    /**
     * Нормализует угол yaw в пределах [-180, +180]
     */
    private static float wrapAngleTo180(float angle) {
        angle %= 360;
        if (angle >= 180) angle -= 360;
        if (angle < -180) angle += 360;
        return angle;
    }
}
