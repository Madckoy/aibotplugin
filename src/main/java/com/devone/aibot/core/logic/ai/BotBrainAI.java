package com.devone.aibot.core.logic.ai;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.*;
import com.devone.aibot.core.logic.tasks.destruction.BotBreakTask;
import com.devone.aibot.utils.BotLogger;

public class BotBrainAI {

    private static final String AI_SERVER_URL = "http://localhost:5000/decide";

    public static void processDecision(Bot bot) {
        try {
            BotLogger.info(true, "🧠 Нейронные связи активированны!");
            //JSONObject botState = new JSONObject();
            //botState.put("inventoryFull", bot.isInventoryFull());
            //botState.put("mobNearby", bot.isMobNearby());
            //botState.put("isNight", bot.isNight());
            //botState.put("currentTask", bot.getCurrentTask().getName());

            //String response = sendRequest(botState.toString());
            //org.json.simple.JSONObject decision = new JSONObject(response);
            //String action = decision.getString("action");

            //executeAction(bot, action);

        } catch (Exception e) {
            BotLogger.info(true,"❌ Ошибка в BotBrainAI: " + e.getMessage());
        }
    }

    private static void executeAction(Bot bot, String action) {
        switch (action) {
            case "hunt_mob":
                bot.addTaskToQueue(new BotHuntMobsTask(bot));
                break;
            case "break_block":
                bot.addTaskToQueue(new BotBreakTask(bot));
                break;
            case "drop_off":
                bot.addTaskToQueue(new BotDropAllTask(bot, null));
                break;
            case "explore":
                bot.addTaskToQueue(new BotExploreTask(bot));
                break;
            default:
                bot.addTaskToQueue(new BotDecisionMakeTask(bot));
                break;
        }
        BotLogger.info(true, "🤖 BotBrainAI выбрал действие: " + action);
    }
}
