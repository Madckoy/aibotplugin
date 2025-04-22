package com.devone.bot.core.task.reactive.container;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.task.passive.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.task.passive.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.task.passive.active.move.BotMoveTask;
import com.devone.bot.core.task.passive.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.reactive.container.params.BotNearbyHostileReactiveContainerParams;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotNearbyHostileReactiveContainer
        extends BotReactiveTaskContainer<BotNearbyHostileReactiveContainerParams> {

    private BotBlockData target;

    public BotNearbyHostileReactiveContainer(Bot bot, BotBlockData hostileMob) {
        super(bot, BotNearbyHostileReactiveContainerParams.class);
        setObjective("Reactive: BotMoveTask + BotHandAttackTask");
        target = hostileMob;
    }

    @Override
    protected void enqueue(Bot bot) {
        BotLogger.debug(getIcon(), true, bot.getId() + " " + icon + " " + getObjective());
        // 1. Идём к мобу
        BotMoveTaskParams walkParams = new BotMoveTaskParams(target);
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
