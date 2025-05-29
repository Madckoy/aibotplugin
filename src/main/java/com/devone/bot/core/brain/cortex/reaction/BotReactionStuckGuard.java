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

    private static final long STUCK_DURATION_MS = 240_000;  // 2 минуты
    private static final double MOVEMENT_THRESHOLD = 1.0;   // Считаем движение, если сменился блок

    @Override
    public Optional<Runnable> validate(Bot bot) {
        if(bot.getNPCNavigator().isNavigating()) { return Optional.empty(); }

        BotPosition currentPos = bot.getNavigator().getPosition();
        long now = System.currentTimeMillis();

        BotPosition lastPos = BotMemoryV2Utils.readValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.POSITION, BotPosition.class);
        Long lastTime = BotMemoryV2Utils.readValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.TIME, Long.class);

        if (lastPos != null && lastTime != null) {
            double distance = currentPos.distanceTo(lastPos);
            if (distance > MOVEMENT_THRESHOLD) {
                // Бот реально сместился достаточно — сбрасываем
                resetWatchdog(bot, currentPos, now);
                return Optional.empty();
            }

            Long duration = now - lastTime;
            Long remaining = (STUCK_DURATION_MS - duration) / 1000L;
            
            BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG,  BotMemoryItem.ItemKey.REMAINING_TIME, remaining);

            if ( remaining < 0 ) {

                BotMemoryV2Utils.writeValue(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.TIME, now);

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
        BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.POSITION, pos);
        BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.TIME, time);
        BotMemoryV2Utils.writeValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.REMAINING_TIME, STUCK_DURATION_MS / 1000L);
    }

    @Override
    public String getName() {
        return "🪤 Застревание на координатах";
    }
}
