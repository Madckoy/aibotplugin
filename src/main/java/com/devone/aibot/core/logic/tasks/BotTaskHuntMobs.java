package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskExploreConfig;
import com.devone.aibot.core.logic.tasks.configs.BotTaskHuntConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.List;

public class BotTaskHuntMobs extends BotTask {

    private int scanRadius;
    private boolean shouldFollowPlayer = false;

    private LivingEntity targetMob = null;

    public BotTaskHuntMobs(Bot bot) {
        super(bot, "👁️");
        this.config = new BotTaskHuntConfig();

        this.scanRadius = ((BotTaskHuntConfig)this.config).getScanRadius();
        envMap = null;
    }

    @Override
    public void executeTask() {
        
        BotLogger.trace("🚀 Запуск задачи охоты для бота " + bot.getId());
        
        setObjective("Looking for the hostile targets");

        // 🔍 Проверяем, есть ли у нас актуальная карта местности
        if (getEnvMap() == null) {
            BotLogger.trace("🔍 Запускаем 3D-сканирование окружающей среды.");
            bot.addTaskToQueue(new BotTaskSonar3D(bot, this, scanRadius, scanRadius));
            isDone = false;
            return;
        }

        if (targetMob == null || targetMob.isDead()) {
            findTarget();
        }

        if (targetMob != null) {

            Location mobLocation = targetMob.getLocation(); // ✅ Берём позицию цели
            BotLogger.debug("🎯 Бот начинает преследование " + targetMob.getType() + " на " + BotStringUtils.formatLocation(mobLocation));

            bot.addTaskToQueue(new BotTaskFollowTarget(bot, targetMob)); // ✅ Передаём координаты

            isDone = true;
            return;
        }

        if (getElapsedTime()>180000) {
            BotLogger.debug(" Устал, охота утомляет.");
            isDone = true;
            return;
        }

        setEnvMap(null);
    }

    private void findTarget() {
        List<Entity> nearbyEntities = bot.getNPCEntity().getNearbyEntities(scanRadius, scanRadius, scanRadius);

        // Ищем мобов
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Monster) {
                targetMob = (LivingEntity) entity;
                BotLogger.debug("🎯 Найдена цель: " + targetMob.getType());
                return;
            }
        }

        // Если мобов нет, попробуем следовать за игроком (если включено)
        if (shouldFollowPlayer) {
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Player) {
                    targetMob = (LivingEntity) entity;
                    BotLogger.debug("🎯 Найден игрок! Начинаем следование.");
                    return;
                }
            }
        }

        BotLogger.debug("❌ Ни одного моба или игрока не найдено.");
        isDone = true;
        return;
    }
}
