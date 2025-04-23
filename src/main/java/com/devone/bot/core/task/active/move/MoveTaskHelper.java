package com.devone.bot.core.task.active.move;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import org.bukkit.Location;

/**
 * Утилитный класс для управления движением бота (BotMoveTask).
 */
public class MoveTaskHelper {

    /**
     * Устанавливает цель движения для NPC.
     *
     * @param bot    бот
     * @param target целевая позиция
     * @param speed  множитель скорости
     * @param log    включить логирование
     */
    public static void setTarget(Bot bot, BotLocation target, float speed, boolean log) {
        if (bot == null || target == null) return;

        Location targetLoc = BotWorldHelper.getWorldLocation(target);

        // Устанавливаем цель в логике бота и NPC
        bot.getNavigator().setTarget(target); // Внутренняя навигация бота
        bot.getNPCNavigator()
            .getLocalParameters()
            .range(1.0f) // Чуть увеличили, чтобы не висло при неидеальной позиции
            .speedModifier(speed);

        bot.getNPCNavigator().setTarget(targetLoc);

        if (log) {
            BotLogger.debug("🏁", true, bot.getId() + " ▶ Двигаемся к: " + target);
        }
    }

    /**
     * Проверяет, находится ли бот в нужной точке.
     *
     * @param bot        бот
     * @param target     ожидаемая позиция
     * @param yTolerance допустимая погрешность по Y
     * @return true, если бот в нужной позиции
     */
    public static boolean isAtTarget(Bot bot, BotLocation target, double yTolerance) {
        if (bot == null || target == null) return false;

        Location loc = bot.getNPC().getEntity().getLocation();
        BotLocation current = new BotLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        boolean match = current.getX() == target.getX()
                && current.getZ() == target.getZ()
                && Math.abs(current.getY() - target.getY()) <= yTolerance;

        BotLogger.debug("📍", true, bot.getId() + " Позиция: " + current + " | Цель: " + target + " | Совпадает: " + match);

        return match;
    }
}
