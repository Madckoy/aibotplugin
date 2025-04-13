package com.devone.bot.core.logic.task.attack.regular;


import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.attack.regular.params.BotRegularAttackParams;
import com.devone.bot.core.logic.task.attack.survival.params.BotSurvivalAttackTaskParams;
import com.devone.bot.core.logic.task.hand.attack.BotHandAttackTask;
import com.devone.bot.core.logic.task.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotRegularAttackTask extends BotTask {

    private BotBlockData target;
    private double damage = 5.0;
    private boolean isLogged = true;

    public BotRegularAttackTask(Bot bot) {
        super(bot, "➴");
        setObjective("Regular attack");
        this.isLogged = true;
    }

    @Override
    public BotRegularAttackTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotRegularAttackParams) {

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

        setObjective("Regular attack on target..." + target);

        // ✋🏻 2. Атака
        BotHandAttackTask handTask = new BotHandAttackTask(bot).configure(new BotHandAttackTaskParams(target, this.damage));

        // 📋 Добавляем в очередь
        bot.addTaskToQueue(handTask);

        BotLogger.info(isLogged, bot.getId() + "➴ Обычная аттака на цель: " + target + " с уроном: " + damage);
        this.stop();
    }

    @Override
    public void stop() {
        this.isDone = true;
    }
}
