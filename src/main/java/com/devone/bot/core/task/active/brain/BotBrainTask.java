package com.devone.bot.core.task.active.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotReactionManager;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.navigator.simulator.BotSimulatorResult;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.brain.params.BotBrainTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
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
        setObjective(params.getObjective() != null ? params.getObjective() : "Think");

        bot.getBrain().setMemoryExpirationMillis(params.getMemoryExpirationMillis());
      
        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogged(), bot.getId() + " 🧠 Brain deciding...");
        
        long rmt = BotUtils.getRemainingTime(startTime, params.getTimeout());
        cycle++;
        setObjective(params.getObjective() + " "+ cycle +" (" + rmt + ")");
        if (rmt <= 0) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ⏱️ Task timeout passed. Start new cycle!");
            startTime = System.currentTimeMillis(); // new cycle                    
        }

        int radius = BotConstants.DEFAULT_SCAN_RADIUS;
        
        Integer scanRadiusFromMem = (Integer) BotMemoryV2Utils.readValueTyped(bot, 
                                    BotMemoryPartition.PartitionKey.NAVIGATION, 
                                    BotMemoryItem.ItemKey.SCAN_RADIUS, Integer.class);     

        if(scanRadiusFromMem!=null) {
            radius = scanRadiusFromMem.intValue();
        }
        
        // 1. Анализ сцены
        List<BotBlockData> candidates = new ArrayList<>();

        try {
            candidates = bot.getNavigator().calculate(BotConstants.DEFAULT_NORMAL_SIGHT_FOV, radius, BotConstants.DEFAULT_SCAN_HEIGHT);
        } catch (Exception e) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " 🆘 Навигационные расчеты не прошли!");   
            return;
        }
        
        // 2. Орбаботка реакций
        Optional<Runnable> reaction = BotReactionManager.checkReactions(bot);       
        if (reaction.isPresent()) {
            reaction.get().run();  // Запускаем реакцию
        }
    }
}