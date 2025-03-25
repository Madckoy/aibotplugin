package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotHuntTaskConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.EntityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BotHuntMobsTask extends BotTask {

    private int scanRadius;
    private boolean shouldFollowPlayer = false;
    private LivingEntity targetMob = null;

    public BotHuntMobsTask(Bot bot) {
        super(bot, "👮‍♂️");
        this.config = new BotHuntTaskConfig(); // ✅ инициализируем родительское поле
        this.scanRadius = ((BotHuntTaskConfig) config).getScanRadius();
    }

    @Override
    public void executeTask() {
        BotLogger.trace(isLogging(),"🚀 Запуск задачи охоты для бота " + bot.getId());

        setObjective("Look for hostile targets");

        if (getBioEntities() == null) {
            BotLogger.trace(isLogging(),"🔍 Запускаем 3D-сканирование живых целей.");
            bot.addTaskToQueue(new BotSonar3DTask(bot, this, scanRadius, 4));
            isDone = false;
            return;
        }

        if (targetMob == null || targetMob.isDead()) {
            findTarget();
        }

        if (targetMob != null) {
            bot.addTaskToQueue(new BotFollowTargetTask(bot, targetMob));
            BotLogger.debug(isLogging(),"🎯 Бот начинает преследование " + targetMob.getType());
            isDone = true;
            return;
        }

        if (getElapsedTime() > 180000) {
            BotLogger.debug(isLogging(),"😴 Устал, охота утомляет.");
            isDone = true;
            return;
        }

        setBioEntities(null); // попробовать ещё раз в следующий такт
    }

    private void findTarget() {
        List<LivingEntity> nearbyEntities = getBioEntities();
        BotHuntTaskConfig huntConfig = (BotHuntTaskConfig) config;

        for (LivingEntity entity : nearbyEntities) {
            if (EntityUtils.isHostileMob(entity)) {
                if (huntConfig.getTargetAggressiveMobs().contains(entity.getType())) {
                    targetMob = entity;
                    BotLogger.debug(isLogging(),"🎯 Найдена враждебная цель: " + targetMob.getType());
                    return;
                }
            }
        }

        if (shouldFollowPlayer) {
            for (LivingEntity entity : nearbyEntities) {
                if (entity instanceof Player) {
                    targetMob = entity;
                    BotLogger.debug(isLogging(),"🎯 Найден игрок! Начинаем следование.");
                    return;
                }
            }
        }

        BotLogger.debug(isLogging(),"❌ Ни одной подходящей цели не найдено.");
        isDone = true;
    }
}
