package com.devone.bot.core.logic.task.playerlinked.chase;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTaskAutoParams;
import com.devone.bot.core.logic.task.IBotTaskParameterized;
import com.devone.bot.core.logic.task.playerlinked.chase.params.BotChaseTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotChaseTargetTask extends BotTaskAutoParams<BotChaseTaskParams> {

    private BotBlockData target;
    private double chaseDistance;
    private double attackRange;

    private final int updateIntervalTicks = 10;

    public BotChaseTargetTask(Bot bot, BotBlockData target) {
        super(bot, BotChaseTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotChaseTaskParams> setParams(BotChaseTaskParams params) {
        super.setParams(params);
        this.target = params.getTarget();
        this.chaseDistance = params.getChaseDistance();
        this.attackRange = params.getAttackRange();
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        if (target != null) {
            bot.getRuntimeStatus().setTargetLocation(target);
        }

        BotLogger.info("✅", this.isLogging(),
                "Chase parameters: " + target + " | " + chaseDistance + " | " + attackRange);
        return this;
    }

    @Override
    public void execute() {
        if (target == null) {
            BotLogger.info("💀", this.isLogging(), "Цель исчезла. Завершаем преследование.");
            this.stop();
            return;
        }

        setObjective(params.getObjective() + ": " + target);

        updateFollowLogic();

        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), this::execute, updateIntervalTicks);

        if (getElapsedTime() > 120000) {
            BotLogger.info("💀", this.isLogging(), "Не могу добраться до цели. Завершаю преследование.");
            this.stop();
        }
    }

    private void updateFollowLogic() {
        BotUtils.lookAt(bot, target);
        BotLogger.info("🏃", this.isLogging(), "Chasing: " + target);
        this.stop();
    }

    public BotBlockData getFollowingObject() {
        return this.target;
    }
}
