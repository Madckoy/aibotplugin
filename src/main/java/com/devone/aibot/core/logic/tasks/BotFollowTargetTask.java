package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotFollowTaskConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotNavigationUtils;
import com.devone.aibot.utils.BotUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BotFollowTargetTask extends BotTask {

    private LivingEntity target;

    private static final BotFollowTaskConfig config = new BotFollowTaskConfig();
    private final double followDistance = config.getFollowDistance();
    private final double attackRange = config.getAttackRange();

    private final int updateIntervalTicks = 10; // каждые 0.5 сек
    private final double relocateThreshold = 1.5; // если цель сместилась на это расстояние — обновим маршрут

    private long lastChatTime = 0;
    private Location lastKnownLocation;

    public BotFollowTargetTask(Bot bot, LivingEntity target) {
        super(bot, "🎯");
        this.target = target;
        bot.getRuntimeStatus().setTargetLocation(target.getLocation());   
        this.lastKnownLocation = target.getLocation();
        this.isLogged = config.isLogged();
    }

    @Override
    public void execute() {
        if (target == null || target.isDead()) {
            BotLogger.info(this.isLogged(),"💀 Цель исчезла. Завершаем преследование.");
            this.stop();
            return;
        }

        setObjective("Chase the target: " + target.getType());
        
        updateFollowLogic();

        // Повторим проверку через заданный интервал
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), this::execute, updateIntervalTicks);

        // Защита от вечного цикла
        if (getElapsedTime() > 120000) {
            BotLogger.info(this.isLogged(),"💀 Не могу добраться до цели. Завершаю преследование.");
            this.stop();
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
            updateNavigation(player.getLocation());
            BotLogger.info(this.isLogged(),"🏃 Бот следует за игроком " + player.getName());
        }

        if (System.currentTimeMillis() - lastChatTime > 10000) {
            bot.addTaskToQueue(new BotTalkTask(bot, player, BotTalkTask.TalkType.COMPLIMENT));
            lastChatTime = System.currentTimeMillis();
        }
    }

    private void followAndAttack(double distance) {
        
        //BotUtils.lookAt(bot, target.getLocation());

        if (distance > attackRange) {
            updateNavigation(target.getLocation());
        
            BotLogger.info(this.isLogged(),"🏃 Преследуем " + target.getType() + " (расстояние: " + distance + ")");
        } else {
            attackTarget();
            this.stop();
        }
    }

    private void updateNavigation(Location newTargetLocation) {

            BotNavigationUtils.navigateTo(bot, lastKnownLocation, 2.5);
            
            BotLogger.info(this.isLogged(),"🔄 Обновляем маршрут к новой позиции цели.");
  
    }

    private void attackTarget() {
        if (target == null || target.isDead()) return;

        double distance = bot.getRuntimeStatus().getCurrentLocation().distance(target.getLocation());

        if (distance <= attackRange) {
            BotUseHandTask hand_task = new BotUseHandTask(bot, "⚔️");
            hand_task.configure(target.getLocation(), target, 10);
            bot.addTaskToQueue(hand_task);
            BotLogger.info(this.isLogged(),"⚔️ Бот атакует " + target.getType() + "!");
        }
    }

    public LivingEntity getFollowingObject() {
        return this.target;
    }

    @Override
    public void stop() {
        this.isDone = true;
    }

}
