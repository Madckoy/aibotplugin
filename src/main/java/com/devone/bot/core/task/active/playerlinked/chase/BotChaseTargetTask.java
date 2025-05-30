package com.devone.bot.core.task.active.playerlinked.chase;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.playerlinked.chase.params.BotChaseTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotChaseTargetTask extends BotTaskAutoParams<BotChaseTaskParams> {

    private BotBlockData target;
    private double chaseDistance;
    private double attackRange;

    private final int updateIntervalTicks = 10;
    private boolean chasing = false;

    public BotChaseTargetTask(Bot bot, BotBlockData target) {
        super(bot, BotChaseTaskParams.class);
        this.target = target;
    }

    @Override
    public IBotTaskParameterized<BotChaseTaskParams> setParams(BotChaseTaskParams params) {
        super.setParams(params);
        this.target = params.getTarget();
        this.chaseDistance = params.getChaseDistance();
        this.attackRange = params.getAttackRange();
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        if (target == null) {
            BotLogger.debug("💀", isLogged(), "❌ Цель отсутствует. Завершаем задачу.");
            stop();
            return;
        }

        setObjective(params.getObjective() + ": " + target.getType() + " at " + target.getPosition().toCompactString());
        chasing = true;

        scheduleNextChaseCycle();
    }

    private void scheduleNextChaseCycle() {
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {

            if (!chasing || target == null) {
                BotLogger.debug("💀", isLogged(), "❌ Преследование завершено (null/invalid)");
                stop();
                return;
            }

            double distance = bot.getNavigator().getPosition().distanceTo(target.getPosition());

            if (distance > chaseDistance) {
                BotLogger.debug("🚫", isLogged(), "Цель вне зоны преследования: " + distance);
                stop();
                return;
            }

            // 🔁 продолжаем преследование
            BotLogger.debug("🏃", isLogged(), "Преследуем: " + target + " на расстоянии " + distance);
            BotUtils.turnToTargetSync(this, bot, target.getPosition());
            bot.getNavigator().setTarget(target);

            if (distance <= attackRange) {
                BotLogger.debug("🎯", isLogged(), "Цель достигнута. Завершаем.");
                stop();
                return;
            }

            if (getElapsedTime() > 120000) {
                BotLogger.debug("⏳", isLogged(), "Таймаут преследования. Завершаем.");
                stop();
                return;
            }

            // 📆 Назначаем следующий шаг
            scheduleNextChaseCycle();

        }, updateIntervalTicks);
    }

    @Override
    public void stop() {
        chasing = false;
        super.stop();
    }

    public BotBlockData getFollowingObject() {
        return this.target;
    }
}
