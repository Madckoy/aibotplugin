package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.core.logic.tasks.configs.BotTaskExploreConfig;
import com.devone.aibot.core.logic.tasks.configs.BotTaskHuntConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.utils.BotEnv3DScan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class BotTaskHuntMobs extends BotTask {

    private LivingEntity targetMob = null;
    private double attackRange = 2.0; // Дальность атаки в ближнем бою
    private boolean shouldPickupLoot = true;
    private int searchRadius = 15; // Радиус поиска мобов
    private Set<EntityType> targetMobs = null; // Целевые мобы

    public BotTaskHuntMobs(Bot bot) {
        super(bot, "⚔️");
        config = new BotTaskHuntConfig();

        // Загружаем параметры из конфига
        if (config.isEnabled()) {
            searchRadius = ((BotTaskExploreConfig) config).getScanRadius();
            shouldPickupLoot = ((BotTaskHuntConfig) config).shouldPickupLoot();
            targetMobs = ((BotTaskHuntConfig) config).getTargetAgressiveMobs();
    }
    }

    @Override
    public BotTask configure(Object... params) {
        super.configure(params);
        
        if (params.length >= 1 && params[0] instanceof Set) {
            targetMobs = (Set<EntityType>) params[0];
            if (targetMobs.isEmpty()) targetMobs = null; // Если передан пустой сет, охотимся на всех мобов
        }
        if (params.length >= 2 && params[1] instanceof Integer) {
            this.searchRadius = (Integer) params[1];
        }
        if (params.length >= 3 && params[2] instanceof Boolean) {
            this.shouldPickupLoot = (Boolean) params[2];
        }

        bot.setAutoPickupEnabled(shouldPickupLoot);
        BotLogger.debug("⚙️ BotTaskHunt настроен: " + (targetMobs == null ? "ВСЕ МОБЫ" : targetMobs)); 
        return this;
    }

    @Override
    public void executeTask() {
        BotLogger.trace("🚀 Запуск задачи охоты для бота " + bot.getId());

        if (isInventoryFull()) {
            BotLogger.trace("⛔ Инвентарь полон, охота завершена.");
            isDone = true;
            return;
        }

        bot.pickupNearbyItems(shouldPickupLoot);

        // 🔍 Проверяем, есть ли у нас актуальная карта местности
        if (getEnvMap() == null) {
            BotLogger.trace("🔍 Запускаем 3D-сканирование окружающей среды.");
            bot.addTaskToQueue(new BotTaskSonar3D(bot, this, searchRadius, 4));
            isDone = false;
            return;
        }

        if (targetMob == null || targetMob.isDead()) {
            findTargetMob();
        }

        if (targetMob != null) {
            moveToTargetMob();
            attackTargetMob();
        } else {
            BotLogger.trace("❌ Целей нет, завершаем охоту.");
            isDone = true;
        }
    }

    private void findTargetMob() {
        List<Entity> nearbyEntities = bot.getNPCEntity().getNearbyEntities(searchRadius, searchRadius, searchRadius);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Monster) { // Только агрессивные мобы
                if (targetMobs == null || targetMobs.contains(entity.getType())) { // Фильтр по целевым мобам
                    targetMob = (LivingEntity) entity;
                    BotLogger.debug("🎯 Найдена цель: " + targetMob.getType());
                    break;
                }
            }
        }
    }

    private void moveToTargetMob() {
        if (targetMob == null) return;

        Location mobLocation = targetMob.getLocation();
        double distance = bot.getNPCCurrentLocation().distance(mobLocation);

        if (distance > attackRange) {
            Bot.navigateTo(bot, mobLocation);
            BotLogger.trace("🚶 Двигаемся к мобу: " + targetMob.getType());
        }
    }

    private void attackTargetMob() {
        if (targetMob == null) return;

        double distance = bot.getNPCCurrentLocation().distance(targetMob.getLocation());

        if (distance <= attackRange) {
            targetMob.damage(5); // Урон (можно адаптировать)
            animateHand();
            BotLogger.debug("⚔️ Бот атаковал " + targetMob.getType());
        }
    }

    private boolean isInventoryFull() {
        boolean full = !BotInventory.hasFreeInventorySpace(bot, null);
        return full;
    }

    private void animateHand() {
        if (bot.getNPCEntity() instanceof Player) {
            Player playerBot = (Player) bot.getNPCEntity();
            playerBot.swingMainHand();
        }
    }
}
