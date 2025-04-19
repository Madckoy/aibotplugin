
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
        double health = bot.getState().getHealth();
        BotLogger.debug("🤖", true, bot.getId() + " 💔 Проверка реакции на здоровье. HP = " + health);

        if (health < 5.0) {
            if (BotReactiveUtils.isAlreadyReacting(bot)) {
                return BotReactiveUtils.avoidOverReaction(bot);
            }

            BotReactiveUtils.activateReaction(bot);

            return Optional.of(() -> {
                BotLogger.debug("🤖", true, bot.getId() + " 💔 Здоровье критически низкое. Ищу безопасное место...");

                BotTeleportTask tpTask = new BotTeleportTask(bot, null);
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
