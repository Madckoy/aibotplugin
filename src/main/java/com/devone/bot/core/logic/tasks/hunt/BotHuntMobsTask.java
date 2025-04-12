package com.devone.bot.core.logic.tasks.hunt;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.hunt.config.BotHuntTaskConfig;
import com.devone.bot.core.logic.tasks.playerlinked.follow.BotFollowTargetTask;
import com.devone.bot.core.logic.tasks.sonar.BotSonar3DTask;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotHuntMobsTask extends BotTask {

    private int scanRadius;
    private BotBlockData target = null;

    public BotHuntMobsTask(Bot bot) {
        super(bot, "😈");
        this.config = new BotHuntTaskConfig(); // ✅ инициализируем родительское поле
        this.scanRadius = ((BotHuntTaskConfig) config).getScanRadius();
        this.isLogged = config.isLogged();
        setObjective("Look for hostile targets");
    }

    @Override
    public void execute() {
        BotLogger.info(isLogged(),"🚀 Запуск задачи охоты для бота " + bot.getId());

        setObjective("Look for hostile targets");

        if (getSceneData() == null) {
            BotLogger.info(this.isLogged(),"🔍 Запускаем 3D-сканирование живых целей.");
            bot.addTaskToQueue(new BotSonar3DTask(bot, this, scanRadius, scanRadius));
            return;
        }

        if (target == null) {
            findTarget();
        }

        if (target != null) {

            bot.addTaskToQueue(new BotFollowTargetTask(bot, target));
            BotLogger.info(this.isLogged(),"🎯 Бот начинает преследование " + target.type);
            this.stop();
            return;
        }

        if (getElapsedTime() > 180000) {
            BotLogger.info(this.isLogged(),"😴 Устал, охота утомляет.");
            this.stop();
            return;
        }

        setSceneData(null); // попробовать ещё раз в следующий такт
    }

    private void findTarget() {
        BotLogger.info(this.isLogged(),"❌ Ни одной подходящей цели не найдено.");
        this.stop();
    }

    @Override
    public void stop() {
        isDone = true;
    }

}
