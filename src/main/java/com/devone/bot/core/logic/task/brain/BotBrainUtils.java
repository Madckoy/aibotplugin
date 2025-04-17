package com.devone.bot.core.logic.task.brain;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.scene.BotSceneContext;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.scene.BotSceneData;

public class BotBrainUtils {

    public static boolean detectIfStuck(Bot bot) {

        BotLocation botPos = bot.getMemory().getCurrentLocation();
        BotSceneData sceneData =  bot.getMemory().getSceneData();

        BotSceneContext context = BotNavigationPlannerWrapper.getSceneContext(sceneData.blocks, sceneData.entities,
                botPos);

        // check if has more than one block to visit
        int totalGoals = context.reachableGoals.size();

        if(totalGoals <= 1) {
            //the bot is stuck!
            bot.getMemory().setStuck(true);
            BotLogger.info("🚨", true, "The bot "+bot.getId() + " is stuck!");
            return true;
        } else {
          return false;
        }
    }

}
