package com.devone.bot.web.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.utils.BotStringUtils;
import com.devone.bot.web.BotWebService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BotStatusServlet extends HttpServlet {
    private final BotManager botManager;
    public BotStatusServlet(BotManager botManager) { this.botManager = botManager; }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        JsonObject result = new JsonObject();
        JsonArray botsArray = new JsonArray();
        Collection<Bot> bots = botManager.getAllBots();

        for (Bot bot : bots) {
            JsonObject botJson = new JsonObject();
            Location loc = bot.getRuntimeStatus().getCurrentLocation();
            if (loc != null) {
                botJson.addProperty("skin", "http://" + BotWebService.getServerHost() + ":"+BotWebService.getServerPort()+"/skins/" + bot.getUuid() + ".png");
                botJson.addProperty("id", bot.getId());
                botJson.addProperty("position", BotStringUtils.formatLocation(loc));
                botJson.addProperty("task", bot.getCurrentTask().getName());
                botJson.addProperty("object", getCurrentObjective(bot));
                Location tg_loc = bot.getRuntimeStatus().getTargetLocation();
                botJson.addProperty("target", BotStringUtils.formatLocation(tg_loc));
                botJson.addProperty("elapsedTime", BotStringUtils.formatTime(bot.getCurrentTask().getElapsedTime()));

                List<BotTask> taskStack = (bot.getLifeCycle() != null && bot.getLifeCycle().getTaskStackManager() != null)
                    ? new ArrayList<>(bot.getLifeCycle().getTaskStackManager().getTaskStack())
                    : new ArrayList<>();
                String taskStackText = taskStack.isEmpty() ? "N/A" :
                    taskStack.stream().map(BotTask::getName).collect(Collectors.joining(" ➜ "));
                botJson.addProperty("queue", taskStackText);
                botsArray.add(botJson);


                // 📦 Serialize inventory
                ItemStack[] contents = bot.getInventory().getNPCInventory().getContents();
                JsonArray inventoryArray = new JsonArray();

                for (ItemStack item : contents) {
                    if (item != null && item.getAmount() > 0) {
                        JsonObject slotObj = new JsonObject();
                        slotObj.addProperty("type", item.getType().toString().toLowerCase());
                        slotObj.addProperty("amount", item.getAmount());
                        inventoryArray.add(slotObj);
                    }
                }

                botJson.add("inventorySlotsFilled", inventoryArray);

                int count = Arrays.stream(contents)
                                .filter(Objects::nonNull)
                                .mapToInt(ItemStack::getAmount)
                                .sum();

                botJson.addProperty("inventoryCount", count);
                botJson.addProperty("inventoryMax", 36); // или сколько слотов у тебя по факту

            }
        }

        result.add("bots", botsArray);
        resp.getWriter().write(result.toString());
    }
    
    private static String getCurrentObjective(Bot bot) {
        BotTask currentTask = bot.getCurrentTask();
        return (currentTask != null) ? currentTask.getObjective() : "";
    }
}