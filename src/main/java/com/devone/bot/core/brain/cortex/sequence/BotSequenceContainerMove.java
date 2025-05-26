package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceContainerMoveParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceContainerMove extends BotSequenceContainer<BotSequenceContainerMoveParams> {

    public BotSequenceContainerMove(Bot bot) {

        super(bot, BotSequenceContainerMoveParams.class);
        setIcon("🔀");
        setObjective("Sequence: Bot Move Task");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());
        
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
