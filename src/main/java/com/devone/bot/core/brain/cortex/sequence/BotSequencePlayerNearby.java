package com.devone.bot.core.brain.cortex.sequence;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequencePlayerNearbyParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.drop.BotDropAllTask;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class BotSequencePlayerNearby extends BotSequenceContainer<BotSequencePlayerNearbyParams> {

    Player player = null;

    public BotSequencePlayerNearby(Bot bot, Player player) {
        super(bot, BotSequencePlayerNearbyParams.class);
        this.player = player;
        setIcon("🔀");
        setObjective("Sequence: Bot MoveTask and Bot Drop All Task");
        setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        BotLogger.debug(getIcon(), isLogged(), bot.getId() + " " + icon + " " + getObjective());
      
        BotBlockData plBlock = BotWorldHelper.blockToBotBlockData(player.getLocation().getBlock());

        // 1. Идём к игроку
        BotMoveTask walkTask = new BotMoveTask(bot);
        bot.getNavigator().setTarget(plBlock);
        walkTask.setObjective("🥾 Идём к игроку");

        // 2. Дропаем ресы
        BotDropAllTask dropTask = new BotDropAllTask(bot, player);
        dropTask.setObjective("🎁 Передаём ресурсы");

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(walkTask);
        subtasks.add(dropTask);
        return subtasks;
    }
}
