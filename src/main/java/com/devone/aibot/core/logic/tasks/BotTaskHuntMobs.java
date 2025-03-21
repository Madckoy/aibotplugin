package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskExploreConfig;
import com.devone.aibot.core.logic.tasks.configs.BotTaskFollowConfig;
import com.devone.aibot.core.logic.tasks.configs.BotTaskHuntConfig;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.entity.*;

import java.util.List;

public class BotTaskHuntMobs extends BotTask {

    private int scanRadius;
    private boolean shouldFollowPlayer = false;

    private LivingEntity targetMob = null;

    public BotTaskHuntMobs(Bot bot) {
        super(bot, "⚔️");
        config = new BotTaskHuntConfig();

        scanRadius = ((BotTaskExploreConfig)config).getScanRadius();

        setObjective("Looking for the hostile targets");
    }

    @Override
    public void executeTask() {
        
        BotLogger.trace("🚀 Запуск задачи охоты для бота " + bot.getId());

        // 🔍 Проверяем, есть ли у нас актуальная карта местности
        if (getEnvMap() == null) {
            BotLogger.trace("🔍 Запускаем 3D-сканирование окружающей среды.");
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
        } else {
            BotLogger.debug("❌ Целей нет, переходим в патрулирование.");
            bot.addTaskToQueue(new BotTaskExplore(bot));
            isDone = true;
        }
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
    }
}
