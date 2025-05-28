package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceExcavate;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotReactionStuckGuard implements IBotReaction {

    private static final long STUCK_DURATION_MS = 120_000;  // 2 минуты
    private static final double MOVEMENT_THRESHOLD = 1.0;   // Считаем движение, если сменился блок
    private static final String MEM_PART = BotMemoryPartition.PartitionKey.WATCHDOG.toString();
    private static final String KEY_POS = BotMemoryItem.ItemKey.POSITION_KEY.toString();
    private static final String KEY_TIME = BotMemoryItem.ItemKey.TIME.toString();

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotPosition currentPos = bot.getNavigator().getPosition();
        long now = System.currentTimeMillis();

        BotPosition lastPos = BotMemoryV2Utils.readMemoryValueTyped(bot, MEM_PART, KEY_POS, BotPosition.class);
        Long lastTime = BotMemoryV2Utils.readMemoryValueTyped(bot, MEM_PART, KEY_TIME, Long.class);

        if (lastPos != null && lastTime != null) {
            double distance = currentPos.distanceTo(lastPos);
            if (distance > MOVEMENT_THRESHOLD) {
                // Бот реально сместился достаточно — сбрасываем
                resetWatchdog(bot, currentPos, now);
                return Optional.empty();
            }

            long duration = now - lastTime;
            if (duration > STUCK_DURATION_MS) {
                BotLogger.debug("🪤", bot.isLogged(), bot.getId() +
                        " ❗ Застрял на позиции " + currentPos +
                        " (расстояние: " + String.format("%.2f", distance) + " м) " +
                        "в течение " + duration + " мс");

                return Optional.of(() ->
                        BotTaskManager.push(bot, new BotSequenceExcavate(bot))
                );
            }

        } else {
            // Инициализация
            resetWatchdog(bot, currentPos, now);
        }

        return Optional.empty();
    }

    private void resetWatchdog(Bot bot, BotPosition pos, long time) {
        BotMemoryV2Utils.memorizeValue(bot, MEM_PART, KEY_POS, pos);
        BotMemoryV2Utils.memorizeValue(bot, MEM_PART, KEY_TIME, time);
    }

    @Override
    public String getName() {
        return "🪤 Застревание на координатах";
    }
}
