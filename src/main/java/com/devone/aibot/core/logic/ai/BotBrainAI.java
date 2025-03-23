package com.devone.aibot.core.logic.ai;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.*;
import com.devone.aibot.core.logic.tasks.destruction.BotTaskBreakBlock;
import com.devone.aibot.utils.BotLogger;

public class BotBrainAI {

    private static final String AI_SERVER_URL = "http://localhost:5000/decide";

    public static void processDecision(Bot bot) {
        try {
            BotLogger.info("🧠 Нейронные связи активированны!");
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
            BotLogger.error("❌ Ошибка в BotBrainAI: " + e.getMessage());
        }
    }

    private static void executeAction(Bot bot, String action) {
        switch (action) {
            case "hunt_mob":
                bot.addTaskToQueue(new BotTaskHuntMobs(bot));
                break;
            case "break_block":
                bot.addTaskToQueue(new BotTaskBreakBlock(bot));
                break;
            case "drop_off":
                bot.addTaskToQueue(new BotTaskDropAll(bot, null));
                break;
            case "explore":
                bot.addTaskToQueue(new BotTaskExplore(bot));
                break;
            default:
                bot.addTaskToQueue(new BotTaskIdle(bot));
                break;
        }
        BotLogger.debug("🤖 BotBrainAI выбрал действие: " + action);
    }
}
