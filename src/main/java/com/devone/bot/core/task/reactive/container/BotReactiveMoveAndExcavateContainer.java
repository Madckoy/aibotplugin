package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.excavate.BotExcavateTask;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.excavate.params.BotExcavateTaskParams;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveMoveAndExcavateContainerParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveMoveAndExcavateContainer extends BotReactiveTaskContainer<BotReactiveMoveAndExcavateContainerParams> {

    public BotReactiveMoveAndExcavateContainer(Bot bot) {

        super(bot, BotReactiveMoveAndExcavateContainerParams.class);

        setObjective("Reactive: Bot Move and Excavate Tasks");
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
        
        if(params.position!=null) {
            BotPosition movePosiiton = new BotPosition(params.position);

            BotMoveTaskParams mv_params = new BotMoveTaskParams();
            mv_params.setTarget(new BotPosition(movePosiiton));
            BotMoveTask moveTask =  new BotMoveTask(bot);
            moveTask.setParams(mv_params);
            add(moveTask);
        }

        BotExcavateTaskParams excvParams = new BotExcavateTaskParams();
        BotExcavateTask excvTask = new BotExcavateTask(bot);
        excvTask.setParams(excvParams);
        add(excvTask);
    }

}
