package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotFollowTaskConfig;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotLogger;

import com.devone.bot.utils.BotUtils;

public class BotFollowTargetTask extends BotTask {

    private BotBlockData target;

    private static final BotFollowTaskConfig config = new BotFollowTaskConfig();
    private final double followDistance = config.getFollowDistance();
    private final double attackRange = config.getAttackRange();

    private final int updateIntervalTicks = 10; // каждые 0.5 сек

    public BotFollowTargetTask(Bot bot, BotBlockData target) {
        super(bot, "🎯");
        this.target = target;
        bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());   
        this.isLogged = config.isLogged();
    }

    @Override
    public void execute() {


        if (target == null) {
            BotLogger.info(this.isLogged(),"💀 Цель исчезла. Завершаем преследование.");
            this.stop();
            return;
        }

        setObjective("Chase the target: " + target.type);
        
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

        followAndAttack();

    }

    private void followAndAttack() {
        
        BotUtils.lookAt(bot, target.getCoordinate3D());
        
        BotLogger.info(this.isLogged(),"🏃 Преследуем " + target.type );

        this.stop();
    }

    public BotBlockData getFollowingObject() {
        return this.target;
    }

    @Override
    public void stop() {
        this.isDone = true;
    }

}
