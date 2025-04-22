package com.devone.bot.core.task.active.move;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.utils.logger.BotLogger;
import org.bukkit.Location;

/**
 * Утилитный класс для управления движением бота (BotMoveTask).
 */
public class MoveTaskHelper {

    /**
     * Устанавливает цель движения NPC через Citizens, с нужной точностью и скоростью.
     */
    public static void setTarget(Bot bot, BotLocation target, float speed, boolean log) {
        if (bot == null || target == null) return;

        bot.getNavigator().setTarget(target); // Для внутренней логики бота
        bot.getNPCNavigator().getLocalParameters()
            .range(0.5f) // максимальная допустимая дистанция до цели
            .speedModifier(speed);

        Location targetLoc = BotWorldHelper.getWorldLocation(target);
        bot.getNPCNavigator().setTarget(targetLoc);

        if (log) {
            BotLogger.debug("🏁", true, bot.getId() + " ▶ Устанавливаем цель движения: " + target);
        }
    }

    /**
     * Проверяет, действительно ли бот находится в целевой точке.
     *
     * @param bot       бот
     * @param target    ожидаемая координата
     * @param yTolerance допустимая разница по Y (например, 0 или 1)
     * @return true, если бот стоит в нужной точке (с учётом Y-погрешности)
     */
    public static boolean isAtTarget(Bot bot, BotLocation target, double yTolerance) {
        if (bot == null || target == null) return false;

        Location loc = bot.getNPC().getEntity().getLocation();
        BotLocation current = new BotLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        boolean match = current.getX() == target.getX()
                && current.getZ() == target.getZ()
                && Math.abs(current.getY() - target.getY()) <= yTolerance;

        BotLogger.debug("📍", true, bot.getId() + " Сравнение позиции: текущая = " + current
                + " | цель = " + target + " | совпадает: " + match);

        return match;
    }
}
