package com.devone.bot.core.logic.navigation.selectors;

import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;

import java.util.Comparator;
import java.util.List;

public class BotBioSelector {

    /**
     * Выбирает ближайшую цель из списка.
     *
     * @param targets   список целей
     * @param botOrigin координаты бота
     * @return ближайшая цель или null, если список пуст
     */
    public static BotBlockData pickNearestTarget(List<BotBlockData> targets, BotLocation botOrigin) {
        if (targets == null || targets.isEmpty() || botOrigin == null) return null;

        return targets.stream()
            .min(Comparator.comparingDouble(target -> target.distanceTo(botOrigin)))
            .orElse(null);
    }

    /**
     * Выбирает ближайшую враждебную сущность.
     */
    public static BotBlockData pickNearestHostile(List<BotBlockData> entities, BotLocation botOrigin) {
        return pickNearestTarget(
            entities.stream()
                    .filter(BotBlockData::isHostileMob)
                    .toList(),
            botOrigin
        );
    }

    /**
     * Выбирает ближайшую пассивную сущность.
     */
    public static BotBlockData pickNearestPassive(List<BotBlockData> entities, BotLocation botOrigin) {
        return pickNearestTarget(
            entities.stream()
                    .filter(BotBlockData::isPassiveMob)
                    .toList(),
            botOrigin
        );
    }
}
