package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotNearbyHostileReactionContainerParams;
import com.devone.bot.core.utils.blocks.BotBlockData;

public class BotNearbyHostileReactionContainer extends BotReactiveTaskContainer<BotNearbyHostileReactionContainerParams> {

    public BotNearbyHostileReactionContainer(Bot bot, BotBlockData hostileMob) {
        super(bot, BotNearbyHostileReactionContainerParams.class);
        setIcon("⚔️");
        setObjective("Реакция на врага — удар рукой");
    }

    @Override
    protected void enqueue(Bot bot) {
        bot.reactiveTaskStart(new BotHandAttackTask(bot));
    }
}
