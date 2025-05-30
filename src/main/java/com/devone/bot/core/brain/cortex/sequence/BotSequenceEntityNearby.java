package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceEntityNearbyParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.task.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSequenceEntityNearby
        extends BotSequenceContainer<BotSequenceEntityNearbyParams> {

    private BotBlockData target;

    public BotSequenceEntityNearby(Bot bot, BotBlockData hostileMob) {
        super(bot, BotSequenceEntityNearbyParams.class);
        //setIcon("🔀");
        setObjective("Sequence: Bot MoveTask and Bot Hand Attack Task");
        setDeffered(true);
        target = hostileMob;
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());
        // 1. Идём к мобу
         
        BotMoveTask walkTask = new BotMoveTask(bot);
        walkTask.setTarget(target.getPosition());        
        walkTask.setObjective("🥾 Approach the entity: " + target.getType());

        // 2. Атакуем
        BotHandAttackTask attackTask = new BotHandAttackTask(bot);
        attackTask.setTarget(target);

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(walkTask);
        subtasks.add(attackTask);
        return subtasks;
    }
}
