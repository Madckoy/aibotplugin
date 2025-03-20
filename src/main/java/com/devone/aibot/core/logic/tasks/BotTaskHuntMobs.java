package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskFollowConfig;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.entity.*;

import java.util.List;

public class BotTaskHuntMobs extends BotTask {

    private LivingEntity targetMob = null;
    private static final BotTaskFollowConfig config = new BotTaskFollowConfig();
    private final int searchRadius = (int) config.getFollowDistance() * 10; // Динамический радиус поиска мобов
    private final boolean shouldFollowPlayer = true; // Можно вынести в конфиг

    public BotTaskHuntMobs(Bot bot) {
        super(bot, "⚔️");
    }

    @Override
    public void executeTask() {
        BotLogger.trace("🚀 Запуск задачи охоты для бота " + bot.getId());

        // 🔍 Проверяем, есть ли у нас актуальная карта местности
        if (getEnvMap() == null) {
            BotLogger.trace("🔍 Запускаем 3D-сканирование окружающей среды.");
            bot.addTaskToQueue(new BotTaskSonar3D(bot, this, searchRadius, 4));
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
        List<Entity> nearbyEntities = bot.getNPCEntity().getNearbyEntities(searchRadius, searchRadius, searchRadius);

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
