package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.task.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotReactiveNearbyHostileContainerParams;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotReactiveNearbyHostileContainer
        extends BotReactiveTaskContainer<BotReactiveNearbyHostileContainerParams> {

    private BotBlockData target;

    public BotReactiveNearbyHostileContainer(Bot bot, BotBlockData hostileMob) {
        super(bot, BotReactiveNearbyHostileContainerParams.class);
        setObjective("Reactive: Bot MoveTask and Bot Hand Attack Task");
        target = hostileMob;
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
        // 1. Идём к мобу
        BotMoveTaskParams walkParams = new BotMoveTaskParams(target.getPosition());
        BotMoveTask walkTask = new BotMoveTask(bot);
        walkTask.setParams(walkParams);
        walkTask.setObjective("🥾 Идём к мобу");
        add(walkTask);

        // 2. Атакуем
        BotHandAttackTask task = new BotHandAttackTask(bot);
        BotHandAttackTaskParams params = new BotHandAttackTaskParams();
        params.setTarget(target);
        task.setParams(params);

        add(task);
    }
}
