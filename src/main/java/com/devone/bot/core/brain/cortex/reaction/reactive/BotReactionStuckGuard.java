package com.devone.bot.core.brain.cortex.reaction.reactive;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.reaction.IBotReaction;
import com.devone.bot.core.brain.cortex.sequence.BotSequenceContainerExcavate;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

import java.util.Optional;

public class BotReactionStuckGuard implements IBotReaction {

    private static final long STUCK_DURATION_MS = 60000;  // 60 секунд без движения
    private static final double POSITION_TOLERANCE = 1.5; // Допуск в блоке

    @Override
    public Optional<Runnable> validate(Bot bot) {
        BotLogger.debug("🪤", bot.isLogged(), bot.getId() + " 📍 Проверка застревания...");

        BotPosition currentPos = bot.getNavigator().getPosition();
        long currentTime = System.currentTimeMillis();

        BotPosition lastPos = (BotPosition) BotMemoryV2Utils.readMemoryValueTyped(bot, 
                                BotMemoryPartition.PartitionKey.WATCHDOG.toString(),
                                BotMemoryItem.ItemKey.POSITION.toString(),  BotPosition.class);


        Long lastTime = (Long) BotMemoryV2Utils.readMemoryValueTyped(bot, 
                                BotMemoryPartition.PartitionKey.WATCHDOG.toString(),
                                BotMemoryItem.ItemKey.TIME.toString(), Long.class);
        
        if (lastPos != null && lastTime != null) {
            double distance = currentPos.distanceTo(lastPos);
            long duration = currentTime - lastTime;

            if (distance < POSITION_TOLERANCE && duration > STUCK_DURATION_MS) {
                BotLogger.debug("🪤", bot.isLogged(), bot.getId() + " ❗ Бот застрял на " + String.format("%.2f", distance) + " м в течение " + duration + " мс");

                return Optional.of(() -> {
                    BotTaskManager.push(bot, new BotSequenceContainerExcavate(bot));
                });
            }
        }

        // Обновляем позицию и время
        BotMemoryV2Utils.memorizeValue(bot, BotMemoryPartition.PartitionKey.WATCHDOG.toString(), 
                                            BotMemoryItem.ItemKey.POSITION.toString(), currentPos);

        BotMemoryV2Utils.memorizeValue(bot, BotMemoryPartition.PartitionKey.WATCHDOG.toString(), 
                                            BotMemoryItem.ItemKey.TIME.toString(), currentTime);

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "🪤 Застревание в одном блоке";
    }
}
