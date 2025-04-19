
package com.devone.bot.core.bot.task.reactive.reaction;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.reactive.BotReactiveUtils;
import com.devone.bot.core.bot.task.reactive.IBotReactionStrategy;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import java.util.Optional;

public class LowHealthReaction implements IBotReactionStrategy {

    @Override
    public Optional<Runnable> check(Bot bot) {
        
        if(BotReactiveUtils.isAlreadyReacting(bot)){
            return BotReactiveUtils.avoidOverReaction(bot);
        };

        BotReactiveUtils.activateReaction(bot);

        double health = bot.getState().getHealth(); // Пример: нужно иметь метод getHealth()

        if (health < 5.0) {
            return Optional.of(() -> {
                BotLogger.debug("💔", true, "Здоровье критически низкое. Ищу безопасное место...");
                                        // 1. Телепорт
                        BotTeleportTask tpTask = new BotTeleportTask(bot, null); // w/o player
                        BotTeleportTaskParams tpParams = new BotTeleportTaskParams();
                        tpParams.setLocation(BotWorldHelper.getWorldSpawnLocation());
                        tpTask.setParams(tpParams);
                        BotUtils.pushTask(bot, tpTask);

                    });
            }

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Критически низкое здоровье";
    }
}
