package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskFollowConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigationUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BotTaskFollowTarget extends BotTask {

    private LivingEntity target;

    private static final BotTaskFollowConfig config = new BotTaskFollowConfig();
    private final double followDistance = config.getFollowDistance();
    private final double attackRange = config.getAttackRange();

    private final int updateIntervalTicks = 10; // каждые 0.5 сек
    private final double relocateThreshold = 1.5; // если цель сместилась на это расстояние — обновим маршрут

    private long lastChatTime = 0;
    private Location lastKnownLocation;

    public BotTaskFollowTarget(Bot bot, LivingEntity target) {
        super(bot, "🎯");
        this.target = target;
        bot.getRuntimeStatus().setTargetLocation(target.getLocation());   
        this.lastKnownLocation = target.getLocation();
    }

    @Override
    public void executeTask() {
        if (target == null || target.isDead()) {
            BotLogger.debug("💀 Цель исчезла. Завершаем преследование.");
            isDone = true;
            return;
        }

        setObjective("Chase the target: " + target.getType());
        
        updateFollowLogic();

        // Повторим проверку через заданный интервал
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), this::executeTask, updateIntervalTicks);

        // Защита от вечного цикла
        if (getElapsedTime() > 120000) {
            BotLogger.debug("💀 Не могу добраться до цели. Завершаю преследование.");
            isDone = true;
        }
    }

    private void updateFollowLogic() {
        double distance = bot.getRuntimeStatus().getCurrentLocation().distance(target.getLocation());

        if (target instanceof Player player) {
            followPlayer(player, distance);
        } else {
            followAndAttack(distance);
        }
    }

    private void followPlayer(Player player, double distance) {
        if (distance > followDistance) {
            updateNavigationIfNeeded(player.getLocation());
            BotLogger.debug("🏃 Бот следует за игроком " + player.getName());
        }

        if (System.currentTimeMillis() - lastChatTime > 10000) {
            bot.addTaskToQueue(new BotTaskTalk(bot, player, BotTaskTalk.TalkType.COMPLIMENT));
            lastChatTime = System.currentTimeMillis();
        }
    }

    private void followAndAttack(double distance) {
        if (distance > attackRange) {
            updateNavigationIfNeeded(target.getLocation());
            BotLogger.debug("🏃 Преследуем " + target.getType() + " (расстояние: " + distance + ")");
        } else {
            attackTarget();
            isDone = true; // Завершаем после атаки — задача выполнена
        }
    }

    private void updateNavigationIfNeeded(Location newTargetLocation) {
        if (lastKnownLocation.distanceSquared(newTargetLocation) > relocateThreshold * relocateThreshold) {
            lastKnownLocation = newTargetLocation;

            BotNavigationUtils.navigateTo(bot, lastKnownLocation, 2.5);
            
            BotLogger.trace("🔄 Обновляем маршрут к новой позиции цели.");
        }
    }

    private void attackTarget() {
        if (target == null || target.isDead()) return;

        double distance = bot.getRuntimeStatus().getCurrentLocation().distance(target.getLocation());

        if (distance <= attackRange) {
            BotTaskUseHand hand_task = new BotTaskUseHand(bot);
            hand_task.configure(target.getLocation(), target, 10);
            bot.addTaskToQueue(hand_task);
            BotLogger.debug("⚔️ Бот атакует " + target.getType() + "!");
        }
    }

    public LivingEntity getFollowingObject() {
        return this.target;
    }
}
