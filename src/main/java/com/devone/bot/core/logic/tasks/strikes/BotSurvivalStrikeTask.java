package com.devone.bot.core.logic.tasks.strikes;


import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.hand.BotHandTask;
import com.devone.bot.core.logic.tasks.hand.params.BotHandTaskParams;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.core.logic.tasks.strikes.params.BotSurvivalStrikeTaskParams;
import com.devone.bot.core.logic.tasks.teleport.BotTeleportTask;
import com.devone.bot.core.logic.tasks.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotSurvivalStrikeTask extends BotTask {

    private BotBlockData target;
    private double damage = 5.0;
    private boolean isLogged = true;

    public BotSurvivalStrikeTask(Bot bot) {
        super(bot, "⚔️");
        setObjective("Survival strike: Teleport and Strike");
        this.isLogged = true;
    }

    @Override
    public BotSurvivalStrikeTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotSurvivalStrikeTaskParams) {

            this.target = ((BotSurvivalStrikeTaskParams) params).getTarget();
            this.damage = ((BotSurvivalStrikeTaskParams) params).getDamage();

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
        BotHandTask handTask = new BotHandTask(bot).configure(new BotHandTaskParams(target, this.damage));

        // 📋 Добавляем в очередь в обратном порядке: сначала атака, затем телепорт
        bot.addTaskToQueue(handTask);
        bot.addTaskToQueue(tpTask);

        BotLogger.info(isLogged, bot.getId() + " ⚔️ Подготовлен боевой выпад на цель: " + target.uuid);
        this.stop();
    }

    @Override
    public void stop() {
        this.isDone = true;
    }
}
