package com.devone.bot.core.task.active.move;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import org.bukkit.Location;

/**
 * Утилитный класс для управления движением бота (BotMoveTask).
 */
public class BotMoveTaskHelper {

    /**
     * Устанавливает цель движения для NPC.
     *
     * @param bot    бот
     * @param target целевая позиция
     * @param speed  множитель скорости
     * @param log    включить логирование
     */
    public static void setTarget(Bot bot, BotPosition target, float speed, boolean log) {
        if (target == null) {
            BotLogger.debug( BotUtils.getActiveTaskIcon(bot), true, bot.getId() + " 🏁 Не смогли начать движение.");
            return;
        }

        Location loc = BotWorldHelper.botPositionToWorldLocation(target);

        bot.getNPCNavigator().cancelNavigation();
        bot.getNPCNavigator().setPaused(false);
        bot.getNPCNavigator().getDefaultParameters().speedModifier(speed);
        bot.getNPCNavigator().setTarget(loc);

        if (log) {
            BotLogger.debug(BotUtils.getActiveTaskIcon(bot), true, bot.getId() + " ▶ Двигаемся к: " + target);
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
    public static boolean isAtTarget(Bot bot, BotPosition tgt, double yTolerance) {
        if (bot == null || tgt == null) return false;

        Location botLoc = bot.getNPC().getEntity().getLocation();
        Location poiLoc = BotWorldHelper.botPositionToWorldLocation(tgt);

        boolean match = botLoc.getX() == poiLoc.getX()
                && botLoc.getZ() == poiLoc.getZ()
                && Math.abs(botLoc.getY() - poiLoc.getY()) <= yTolerance;

        BotLogger.debug(BotUtils.getActiveTaskIcon(bot), true, bot.getId() + " 📍 Позиция: " + botLoc + " | Цель: " + poiLoc + " | Совпадает: " + match);

        return match;
    }
}
