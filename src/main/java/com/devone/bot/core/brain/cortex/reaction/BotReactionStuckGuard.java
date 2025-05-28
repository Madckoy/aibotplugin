package com.devone.bot.core.brain.cortex.reaction;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceExcavate;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.blocks.BotPositionKey;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotReactionStuckGuard implements IBotReaction {

    private static final long STUCK_DURATION_MS = 120_000;  // 2 минуты
    private static final String MEM_PART = BotMemoryPartition.PartitionKey.WATCHDOG.toString();
    private static final String KEY_POS = BotMemoryItem.ItemKey.POSITION_KEY.toString();
    private static final String KEY_TIME = BotMemoryItem.ItemKey.TIME.toString();

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotPositionKey currentKey = bot.getNavigator().getPosition().toPositionKey();
        long now = System.currentTimeMillis();

        BotPositionKey lastKey = BotMemoryV2Utils.readMemoryValueTyped(bot, MEM_PART, KEY_POS, BotPositionKey.class);
        Long lastTime = BotMemoryV2Utils.readMemoryValueTyped(bot, MEM_PART, KEY_TIME, Long.class);

        if (lastKey != null && lastTime != null) {
            if (!currentKey.equals(lastKey)) {
                // Бот покинул блок → сброс
                resetWatchdog(bot, currentKey, now);
                return Optional.empty();
            }

            long duration = now - lastTime;
            if (duration > STUCK_DURATION_MS) {
                BotLogger.debug("🪤", bot.isLogged(), bot.getId() + 
                    " ❗ Застрял в блоке " + currentKey + 
                    " в течение " + duration + " мс");

                return Optional.of(() ->
                    BotTaskManager.push(bot, new BotSequenceExcavate(bot))
                );
            }

        } else {
            // Первый запуск — инициализация
            resetWatchdog(bot, currentKey, now);
        }

        return Optional.empty();
    }

    private void resetWatchdog(Bot bot, BotPositionKey key, long time) {
        BotMemoryV2Utils.memorizeValue(bot, MEM_PART, KEY_POS, key);
        BotMemoryV2Utils.memorizeValue(bot, MEM_PART, KEY_TIME, time);
    }

    @Override
    public String getName() {
        return "🪤 Застревание в одном блоке";
    }
}
