package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotLowHealthReactionContainerParams;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
    
public class BotLowHealthReactionContainer extends BotReactiveTaskContainer<BotLowHealthReactionContainerParams>{
    
    public BotLowHealthReactionContainer(Bot bot) {
        
        super(bot, BotLowHealthReactionContainerParams.class);

        setIcon("💔");
        setObjective("Телепорт в безопасное место при низком HP");
        }
    
        @Override
        protected void enqueue(Bot bot) {
            BotLogger.debug(getIcon(), true, bot.getId() + " 🛡️ Реакция: телепорт в безопасную зону");
    
            BotTeleportTaskParams tpParams = new BotTeleportTaskParams();
            tpParams.setLocation(BotWorldHelper.getWorldSpawnLocation());
    
            BotTeleportTask tpTask = new BotTeleportTask(bot, null);
            tpTask.setParams(tpParams);
    
            bot.reactiveTaskStart(tpTask);
        }

    }
    