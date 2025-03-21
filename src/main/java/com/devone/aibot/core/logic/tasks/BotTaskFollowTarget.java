package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskFollowConfig;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.Random;

public class BotTaskFollowTarget extends BotTask {

    private LivingEntity target;

    private static final BotTaskFollowConfig config = new BotTaskFollowConfig();
    private final double followDistance = config.getFollowDistance();
    private final double attackRange = config.getAttackRange();
    //private final long chatCooldown = config.getChatCooldown();
    //private final double insultChance = config.getInsultChance();

    private long lastChatTime = 0;
    private final Random random = new Random();

    public BotTaskFollowTarget(Bot bot, LivingEntity target) {
        super(bot, "🎯");
        this.target = target;
        targetLocation = target.getLocation();
    }

    @Override
    public void executeTask() {
        if (target == null || target.isDead()) {
            BotLogger.debug("💀 Цель исчезла. Завершаем преследование.");
            isDone = true;
            return;
        }

        setObjective("Chasing the target: " + target.getType());

        double distance = bot.getNPCCurrentLocation().distance(target.getLocation());

        if (target instanceof Player) {

            followPlayer((Player) target, distance);

        } else {

            followAndAttack(distance);
        }

        if (getElapsedTime()>60000) {
            BotLogger.debug("💀 Не могу добраться до цели. Завершаю преследование.");
            isDone = true;
            return;
        }
    }

    public LivingEntity getFollowingObject() {
        return this.target;
    }
    /**
     * Логика следования за игроком без атаки.
     */
    private void followPlayer(Player player, double distance) {

        if (distance > followDistance) {
            Bot.navigateTo(bot, player.getLocation());
            BotLogger.debug("🏃 Бот следует за игроком " + player.getName());
        }

        // Иногда бот может сказать что-то игроку
        if (System.currentTimeMillis() - lastChatTime > 10000) { // Раз в 10 секунд
            bot.addTaskToQueue(new BotTaskTalk(bot, player, BotTaskTalk.TalkType.COMPLIMENT));
            lastChatTime = System.currentTimeMillis();
        }
    }

    /**
     * Логика преследования и атаки мобов.
     */
    private void followAndAttack(double distance) {
        if (distance > attackRange) {
            Bot.navigateTo(bot, target.getLocation());
            BotLogger.debug("🏃 Преследуем " + target.getType() + " (расстояние: " + distance + ")");
        } else {
            attackTarget();
        }
    }

    /**
     * Логика атаки.
     */
    private void attackTarget() {
        if (target == null || target.isDead()) return;

        double distance = bot.getNPCCurrentLocation().distance(target.getLocation());

        if (distance <= attackRange) {
            target.damage(5);
            animateHand();
            BotLogger.debug("⚔️ Бот атакует " + target.getType() + "!");

            // 30% шанс поругаться на моба
            if (random.nextDouble() < 0.3) {
                bot.addTaskToQueue(new BotTaskTalk(bot, null, BotTaskTalk.TalkType.INSULT_MOB));
            }
        }
    }

    /**
     * Анимация атаки.
     */
    private void animateHand() {
        if (bot.getNPCEntity() instanceof org.bukkit.entity.Player) {
            ((org.bukkit.entity.Player) bot.getNPCEntity()).swingMainHand();
        }
    }
}
