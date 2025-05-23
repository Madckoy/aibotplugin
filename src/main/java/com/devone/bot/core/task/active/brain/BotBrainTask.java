package com.devone.bot.core.task.active.brain;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.navigator.BotNavigator.NavigationSuggestion;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotBrainTask extends BotTaskAutoParams<BotBrainTaskParams> {

    public BotBrainTask(Bot bot) {
        super(bot, null, BotBrainTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotBrainTaskParams> setParams(BotBrainTaskParams params) {
        if (params == null) {
            throw new IllegalArgumentException("BotBrainTask: setParams(...) получил null");
        }

        super.setParams(params);
        setIcon(params.getIcon() != null ? params.getIcon() : "🧠");
        setObjective(params.getObjective() != null ? params.getObjective() : "Think");

        bot.getBrain().setMemoryExpirationMillis(params.getMemoryExpirationMillis());

        BotLogger.debug(icon, isLogging(), bot.getId() + " ⚙️ Параметры загружены: " +
                "explore=" + params.isAllowExploration() + ", " +
                "excavate=" + params.isAllowExcavation() + ", " +
                "violence=" + params.isAllowViolence() + ", " +
                "teleport=" + params.isAllowTeleport());
       

        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogging(), bot.getId() + " 🧠 Brain deciding...");

        // 1. Анализ сцены
        bot.getNavigator().calculate(bot.getBrain().getSceneData(), BotConstants.DEFAULT_NORMAL_SIGHT_FOV);

        // 2. Получение рекомендации
        NavigationSuggestion suggestion = bot.getNavigator().getNavigationSuggestion();

        switch (suggestion) {
            case CHANGE_DIRECTION -> {
                //rotate to the best YAW            
                BotUtils.rotate(this, bot, bot.getNavigator().getBestYaw());               
            }
            case MOVE -> {
                BotBlockData target = bot.getNavigator().getSuggestedTarget();
                bot.getNavigator().setTarget(target);
                float speed = 1.5f;
                bot.getNavigator().navigate(speed);            
            }

            default -> {
                BotLogger.debug(icon, isLogging(), bot.getId() + " 🧘 Brain is idle.");
            }
        }

        stop(); // мозг сделал своё дело на этом тике
    }


}