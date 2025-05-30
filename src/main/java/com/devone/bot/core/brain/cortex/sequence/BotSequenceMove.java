package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceMoveParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceMove extends BotSequenceContainer<BotSequenceMoveParams> {
    
    private BotPosition target;

    public BotSequenceMove(Bot bot, BotPosition tgtBlock) {

        super(bot, BotSequenceMoveParams.class);
        //setIcon("🔀");
        setObjective("Sequence: Bot Move Task");
        //setDeffered(true);

        target = tgtBlock;
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());
        
        if(target!=null) {
            BotMoveTask moveTask =  new BotMoveTask(bot);
            moveTask.setTarget(target);
            
            List<BotTask<?>> subtasks = new ArrayList<>();
            subtasks.add(moveTask);

            return subtasks;
        } else {
            return null;
        }

    }
}
