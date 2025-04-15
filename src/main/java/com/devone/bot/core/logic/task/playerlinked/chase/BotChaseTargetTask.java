package com.devone.bot.core.logic.task.playerlinked.chase;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.core.logic.task.playerlinked.chase.params.BotChaseTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;

public class BotChaseTargetTask extends BotTask {

    private BotBlockData target;

    private final BotChaseTaskParams params = new BotChaseTaskParams();
    @SuppressWarnings("unused")
    private final double chaseDistance = params.getChaseDistance();
    @SuppressWarnings("unused")
    private final double attackRange = params.getAttackRange();

    private final int updateIntervalTicks = 10; // каждые 0.5 сек

    public BotChaseTargetTask(Bot bot, BotBlockData target) {
        super(bot);
        this.target = target;
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());   
    }

    public BotChaseTargetTask configure(IBotTaskParams params) {
        super.configure(params);
        this.params.copyFrom(params);
        icon = this.params.getIcon();
        objective = this.params.getObjective();
        return this;
    }

    @Override
    public void execute() {
        if (target == null) {
            BotLogger.info("💀", this.isLogging(),"Цель исчезла. Завершаем преследование.");
            this.stop();
            return;
        }

        setObjective(params.getObjective()  + ": " + target.type);
        
        updateFollowLogic();

        // Повторим проверку через заданный интервал
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), this::execute, updateIntervalTicks);

        // Защита от вечного цикла
        if (getElapsedTime() > 120000) {
            BotLogger.info("💀", this.isLogging(),"Не могу добраться до цели. Завершаю преследование.");
            this.stop();
        }
    }

    private void updateFollowLogic() {

        BotUtils.lookAt(bot, target.getCoordinate3D());
        
        BotLogger.info("🏃", this.isLogging(),"Chasing: " + target.type );

        this.stop();

    }

    public BotBlockData getFollowingObject() {
        return this.target;
    }

}
