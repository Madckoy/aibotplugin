package com.devone.bot.core.bot.task.reactive.container;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.bot.task.passive.BotReactiveTaskContainer;
import com.devone.bot.core.bot.task.reactive.container.params.BotStrikeReactionContainerParams;

public class BotStrikeReactionContainer extends BotReactiveTaskContainer<BotStrikeReactionContainerParams> {

    public BotStrikeReactionContainer(Bot bot) {
        super(bot, BotStrikeReactionContainerParams.class);
        setIcon("⚔️");
        setObjective("Реакция на врага — удар рукой");
    }

    @Override
    protected void enqueue(Bot bot) {
        bot.reactiveTaskStart(new BotHandAttackTask(bot));
    }
}
