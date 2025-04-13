package com.devone.bot.core.logic.task.attack.survival;


import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.attack.survival.params.BotSurvivalAttackTaskParams;
import com.devone.bot.core.logic.task.hand.attack.BotHandAttackTask;
import com.devone.bot.core.logic.task.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.core.logic.task.teleport.BotTeleportTask;
import com.devone.bot.core.logic.task.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotSurvivalAttackTask extends BotTask {

    private BotBlockData target;
    private double damage = 5.0;
    private boolean isLogged = true;

    public BotSurvivalAttackTask(Bot bot) {
        super(bot, "જ⁀➴");
        setObjective("Survival strike: Teleport and Strike");
        this.isLogged = true;
    }

    @Override
    public BotSurvivalAttackTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotSurvivalAttackTaskParams) {

            this.target = ((BotSurvivalAttackTaskParams) params).getTarget();
            this.damage = ((BotSurvivalAttackTaskParams) params).getDamage();

        } else {
            BotLogger.info(isLogged, bot.getId() + " ❌ Неверные параметры для BotSurvivalStrikeTask");
            this.stop();
        }
        return this;
    }

    @Override
    public void execute() {
        if (target == null || target.uuid == null) {
            BotLogger.info(isLogged, bot.getId() + " ❌ Цель отсутствует или не содержит UUID");
            this.stop();
            return;
        }

        setObjective("Teleporting and killing...");

        // 🗲 1. Телепорт
        BotTeleportTask tpTask = new BotTeleportTask(bot, null).configure(new BotTeleportTaskParams(target.getCoordinate3D()));
        
        // ✋🏻 2. Атака
        BotHandAttackTask handTask = new BotHandAttackTask(bot).configure(new BotHandAttackTaskParams(target, this.damage));

        // 📋 Добавляем в очередь в обратном порядке: сначала атака, затем телепорт
        bot.addTaskToQueue(handTask);
        bot.addTaskToQueue(tpTask);

        BotLogger.info(isLogged, bot.getId() + " જ⁀➴ Подготовлен боевой выпад на цель: " + target.uuid);
        this.stop();
    }

    @Override
    public void stop() {
        this.isDone = true;
    }
}
