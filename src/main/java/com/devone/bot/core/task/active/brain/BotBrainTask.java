package com.devone.bot.core.task.active.brain;

import java.util.List;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotReactionManager;
import com.devone.bot.core.brain.cortex.BotReactionResult;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.task.active.calibrate.BotCalibrateTask;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotBrainTask extends BotTaskAutoParams<BotBrainTaskParams> {

    long cycle = 0;

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
        
        setObjective(params.getObjective() != null ? params.getObjective() : "Thinking");

        bot.getBrain().setMemoryExpirationMillis(params.getMemoryExpirationMillis());
      
        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogged(), bot.getId() + " 🧠 Brain thinking...");
               
        long rmt = BotUtils.getRemainingTime(startTime, params.getTimeout());
        cycle++;
        setObjective(params.getObjective() + " cycle: "+ cycle +" (" + rmt + ")");
        if (rmt <= 0) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ⏱️ Task timeout passed. Start new cycle!");
            startTime = System.currentTimeMillis(); // new cycle                    
        }        
        
        System.out.println(bot.getNavigator().getSuggestion());

        if(bot.getNavigator().getSuggestion()==null) {
            bot.getNavigator().setSuggestion(Suggestion.NAVIGATION_SIMULATE);
        }
        // regular reset 
        if (cycle % 2000 == 0) {
            BotCalibrateTask calibrateTask = new BotCalibrateTask(bot);
            bot.getTaskManager().pushTask(calibrateTask);
        }

        // Обработка реакций
        List<BotReactionResult> reactions = BotReactionManager.checkReactions(bot);
        for (BotReactionResult r : reactions) {
            setObjective(params.getObjective() + " cycle: "+ cycle +" | "+r.name+" (" + rmt + ")"); // или логика по приоритету
            r.action.run();
        }
    }
}