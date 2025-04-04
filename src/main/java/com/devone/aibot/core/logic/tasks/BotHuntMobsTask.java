package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotHuntTaskConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.BotEntityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BotHuntMobsTask extends BotTask {

    private int scanRadius;
    private boolean shouldFollowPlayer = false;
    private LivingEntity targetMob = null;

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

        if (getBioEntities() == null) {
            BotLogger.info(this.isLogged(),"🔍 Запускаем 3D-сканирование живых целей.");
            bot.addTaskToQueue(new BotSonar3DTask(bot, this, scanRadius, 4));
            return;
        }

        if (targetMob == null || targetMob.isDead()) {
            findTarget();
        }

        if (targetMob != null) {

            //BotUtils.lookAt(bot, targetMob.getLocation());

            bot.addTaskToQueue(new BotFollowTargetTask(bot, targetMob));
            BotLogger.info(this.isLogged(),"🎯 Бот начинает преследование " + targetMob.getType());
            this.stop();
            return;
        }

        if (getElapsedTime() > 180000) {
            BotLogger.info(this.isLogged(),"😴 Устал, охота утомляет.");
            this.stop();
            return;
        }

        setBioEntities(null); // попробовать ещё раз в следующий такт
    }

    private void findTarget() {
        List<LivingEntity> nearbyEntities = getBioEntities();
        BotHuntTaskConfig huntConfig = (BotHuntTaskConfig) config;

        for (LivingEntity entity : nearbyEntities) {
            // kill them all
            targetMob = entity;
            BotLogger.info(this.isLogged(),"🎯 Найдена цель: " + targetMob.getType());
            return;

           // if (BotEntityUtils.isHostileMob(entity)) {
            //    if (huntConfig.getTargetAggressiveMobs().contains(entity.getType())) {
               //     targetMob = entity;
                //    BotLogger.info(this.isLogged(),"🎯 Найдена враждебная цель: " + targetMob.getType());
                   // return;
                //}
            //}
        }

        //if (shouldFollowPlayer) {
        //    for (LivingEntity entity : nearbyEntities) {
        //        if (entity instanceof Player) {
        //            targetMob = entity;
        //            BotLogger.info(this.isLogged(),"🎯 Найден игрок! Начинаем следование.");
        //            return;
        //        }
        //    }
        //}

        BotLogger.info(this.isLogged(),"❌ Ни одной подходящей цели не найдено.");
        this.stop();
    }

    @Override
    public void stop() {
        isDone = true;
    }

}
