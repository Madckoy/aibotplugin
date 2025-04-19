package com.devone.bot.core.bot.task.active.attack.survival;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.attack.survival.params.BotSurvivalAttackTaskParams;
import com.devone.bot.core.bot.task.active.brain.BotBrainTask;
import com.devone.bot.core.bot.task.active.hand.attack.BotHandAttackTask;
import com.devone.bot.core.bot.task.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.bot.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.bot.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.task.passive.BotTaskAutoParams;
import com.devone.bot.core.bot.task.passive.IBotTaskParameterized;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotSurvivalAttackTask extends BotTaskAutoParams<BotSurvivalAttackTaskParams> {

    private BotBlockData target;
    private double damage = 5.0;

    public BotSurvivalAttackTask(Bot bot) {
        super(bot, null, BotSurvivalAttackTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotSurvivalAttackTaskParams> setParams(BotSurvivalAttackTaskParams params) {
        this.target = params.getTarget();
        this.damage = params.getDamage();
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        if (target == null || target.getUUID() == null) {
            BotLogger.debug("❌", isLogging(), bot.getId() + "Target is absent or does not contain UUID");
            this.stop();
            return;
        }

        setObjective(params.getObjective() + " at: " + target);

        // 🗲 1. Телепорт
        BotTeleportTask tpTask = new BotTeleportTask(bot, null);
        tpTask.setParams(new BotTeleportTaskParams(target));

        // ✋🏻 2. Атака
        BotHandAttackTask handTask = new BotHandAttackTask(bot);
        handTask.setParams(new BotHandAttackTaskParams(target, this.damage));

        // 📋 Добавляем в очередь в обратном порядке: сначала атака, затем телепорт
        BotUtils.pushTask(bot, handTask);

        BotUtils.pushTask(bot, tpTask);

        BotLogger.debug(icon, isLogging(), bot.getId() + "Prepared Teleport and Attack on: " + target);
        this.stop();
    }
}
