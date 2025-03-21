package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskExploreConfig;
import com.devone.aibot.core.logic.tasks.configs.BotTaskHuntConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.EntityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BotTaskHuntMobs extends BotTask {

    private BotTaskHuntConfig config; // 👈 Храним с нужным типом

    private int scanRadius;
    private boolean shouldFollowPlayer = false;

    private LivingEntity targetMob = null;

    public BotTaskHuntMobs(Bot bot) {
        super(bot, "👁️");
        
        this.config = new BotTaskHuntConfig();
        scanRadius = config.getScanRadius(); // ✅ Теперь всё работает
        geoMap = null;
        bioEntities = null;
    }

    @Override
    public void executeTask() {
        BotLogger.trace("🚀 Запуск задачи охоты для бота " + bot.getId());
        setObjective("Looking for hostile targets");

        if (getBioEntities() == null) {
            BotLogger.trace("🔍 Запускаем 3D-сканирование живых целей.");
            bot.addTaskToQueue(new BotTaskSonar3D(bot, this, scanRadius, 4));
            isDone = false;
            return;
        }

        if (targetMob == null || targetMob.isDead()) {
            findTarget();
        }

        if (targetMob != null) {
            bot.addTaskToQueue(new BotTaskFollowTarget(bot, targetMob));
            BotLogger.debug("🎯 Бот начинает преследование " + targetMob.getType());
            isDone = true;
            return;
        }

        if (getElapsedTime() > 180000) {
            BotLogger.debug("😴 Устал, охота утомляет.");
            isDone = true;
            return;
        }

        setBioEntities(null);
    }

    private void findTarget() {
        List<LivingEntity> nearbyEntities = getBioEntities();

        for (LivingEntity entity : nearbyEntities) {
            if (EntityUtils.isHostileMob(entity)) {
                targetMob = entity;
                BotLogger.debug("🎯 Найдена враждебная цель: " + targetMob.getType());
                return;
            }
        }

        if (shouldFollowPlayer) {
            for (LivingEntity entity : nearbyEntities) {
                if (entity instanceof Player) {
                    targetMob = entity;
                    BotLogger.debug("🎯 Найден игрок! Начинаем следование.");
                    return;
                }
            }
        }

        BotLogger.debug("❌ Ни одной подходящей цели не найдено.");
        isDone = true;
    }
}
