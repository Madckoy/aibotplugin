package com.devone.bot.core.task.active.brain;

import java.util.Optional;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.BotActionSuggestion.Suggestion;
import com.devone.bot.core.brain.cortex.reaction.BotReactionManager;
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

        BotLogger.debug(icon, isLogged(), bot.getId() + " ⚙️ Параметры загружены: " +
                "explore=" + params.isAllowExploration() + ", " +
                "excavate=" + params.isAllowExcavation() + ", " +
                "violence=" + params.isAllowViolence() + ", " +
                "teleport=" + params.isAllowTeleport());
       

        return this;
    }

    @Override
    public void execute() {
        BotLogger.debug(icon, isLogged(), bot.getId() + " 🧠 Brain deciding...");

        int radius = BotConstants.DEFAULT_SCAN_RADIUS;
        
        Integer scanRadiusFromMem = (Integer) BotMemoryV2Utils.readMemoryValueTyped(bot, 
                                    BotMemoryPartition.PartitionKey.NAVIGATION.toString(), 
                                    BotMemoryItem.ItemKey.SCAN_RADIUS.toString(), Integer.class);     

        if(scanRadiusFromMem!=null) {
            radius = scanRadiusFromMem.intValue();
        }
        
        // 1. Анализ сцены
        try {
            bot.getNavigator().calculate(BotConstants.DEFAULT_NORMAL_SIGHT_FOV, radius, BotConstants.DEFAULT_SCAN_HEIGHT);
        } catch (Exception e) {
        }
        
        // 2. Орбаботка реакций
        Optional<Runnable> reaction = BotReactionManager.checkReactions(bot);
        if (reaction.isPresent()) {
            reaction.get().run();  // Запускаем реакцию
            BotLogger.debug(icon, isLogged(), bot.getId() + " 🧠 Реакция активирована — мозг уступает управление");
            stop();
            return;
        }
        
        // 3. Получение рекомендации
        Suggestion suggestion = bot.getNavigator().getSuggestion();
        BotMemoryV2Utils.memorizeValue(bot, BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryItem.ItemKey.SCAN_RADIUS.toString(), radius);

        switch (suggestion) {
            case CHANGE_DIRECTION -> {
                try {
                    //rotate to the best YAW            
                    BotSimulatorResult res = bot.getNavigator().simulate(BotConstants.DEFAULT_NORMAL_SIGHT_FOV, radius, 4);
                    System.out.println(res.yaw + " : " + res.reachables);
                    if(res.status = true) {
                        BotUtils.rotate(this, bot, res.yaw);  
                    }                   
                } catch (Exception e) {
                }
                    return;             
            }    
            case MOVE -> {
                if(!bot.getNavigator().isCalculating()) {
                    bot.getNavigator().setEnabled(false);
                    BotBlockData target = bot.getNavigator().getSuggestedTarget();
                    bot.getNavigator().setTarget(target);
                    bot.getNavigator().setEnabled(true);                    
                    try {
                        bot.getNavigator().navigate(1.5f);                                    
                    } catch (Exception e) {
                        return;
                    }
                }
                return;
            }

            default -> {
                BotLogger.debug(icon, isLogged(), bot.getId() + " 🧘 Brain is idle.");
            }
        }
    }

}