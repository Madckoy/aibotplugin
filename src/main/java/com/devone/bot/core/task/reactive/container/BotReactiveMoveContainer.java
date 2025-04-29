package com.devone.bot.core.task.reactive.container;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveMoveContainerParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveMoveContainer extends BotReactiveContainer<BotReactiveMoveContainerParams> {

    public BotReactiveMoveContainer(Bot bot) {

        super(bot, BotReactiveMoveContainerParams.class);
        setIcon("🔣");
        setObjective("Reactive: Bot Move Task");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
        
        if(params.position!=null) {
            BotPosition movePosiiton = new BotPosition(params.position);

            BotMoveTaskParams mv_params = new BotMoveTaskParams();
            mv_params.setTarget(new BotPosition(movePosiiton));
            BotMoveTask moveTask =  new BotMoveTask(bot);
            moveTask.setParams(mv_params);

            List<BotTask<?>> subtasks = new ArrayList<>();
            subtasks.add(moveTask);
            return subtasks;
        } else {
            return null;
        }

    }
}
