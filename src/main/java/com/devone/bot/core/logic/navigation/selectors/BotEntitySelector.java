package com.devone.bot.core.logic.navigation.selectors;

import java.util.Comparator;
import java.util.List;

import com.devone.bot.core.bot.blocks.BotBlockData;
import com.devone.bot.core.bot.blocks.BotLocation;

public class BotEntitySelector {

    /**
     * Выбирает ближайшую цель из списка.
     *
     * @param targets   список целей
     * @param botOrigin координаты бота
     * @return ближайшая цель или null, если список пуст
     */
    public static BotBlockData pickNearestTarget(List<BotBlockData> targets, BotLocation botOrigin, double distance) {
        if (targets == null || targets.isEmpty() || botOrigin == null) return null;

        return targets.stream()
            .min(Comparator.comparingDouble(target -> target.distanceTo(botOrigin)))
            .orElse(null);
    }

    /**
     * Выбирает ближайшую враждебную сущность.
     */
    public static BotBlockData pickNearestHostile(List<BotBlockData> entities, BotLocation botOrigin, double distance) {
        return pickNearestTarget(
            entities.stream()
                    .filter(BotBlockData::isHostileMob)
                    .toList(),

            botOrigin,
            distance
        );
    }

    /**
     * Выбирает ближайшую пассивную сущность.
     */
    public static BotBlockData pickNearestPassive(List<BotBlockData> entities, BotLocation botOrigin, double distance) {
        return pickNearestTarget(
            entities.stream()
                    .filter(BotBlockData::isPassiveMob)
                    .toList(),
            botOrigin,
            distance
        );
    }

    
    /**
     * Выбирает ближайшую пассивную сущность.
     */
    public static boolean hasHostilesNearby(List<BotBlockData> entities, BotLocation botOrigin, double distance) {
        return true;
        /* *
        return pickNearestTarget(
            entities.stream()
                    .filter(BotBlockData::isPassiveMob)
                    .toList(),
            botOrigin,
            distance
        );
        */
    }
}
