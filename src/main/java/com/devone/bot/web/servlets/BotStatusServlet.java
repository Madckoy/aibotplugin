package com.devone.bot.web.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotCoordinate3D;
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

        String serverTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        result.addProperty("server-time", serverTime);

        // Время Minecraft
        long mcTicks = Bukkit.getWorlds().get(0).getTime();
        int hour = (int)((mcTicks / 1000 + 6) % 24);
        int minute = (int)((mcTicks % 1000) * 60 / 1000);
        String mcTimeFormatted = String.format("%02d:%02d", hour, minute);
        result.addProperty("mc-time", mcTimeFormatted);


        JsonArray botsArray = new JsonArray();
        Collection<Bot> bots = botManager.getAllBots();

        for (Bot bot : bots) {
            JsonObject botJson = new JsonObject();
            BotCoordinate3D loc = bot.getRuntimeStatus().getCurrentLocation();
            if (loc != null) {
                botJson.addProperty("skin", "http://" + BotWebService.getServerHost() + ":"+BotWebService.getServerPort()+"/skins/" + bot.getUuid() + ".png");
                botJson.addProperty("id", bot.getId());

                String currLoc = " " + loc.x + ", " + loc.y + ", " + loc.z;   

                botJson.addProperty("position", currLoc);

                botJson.addProperty("task", bot.getCurrentTask().getName());
                
                botJson.addProperty("object", getCurrentObjective(bot));

                BotCoordinate3D tgtLoc = bot.getRuntimeStatus().getTargetLocation();

                botJson.addProperty("target", tgtLoc != null ? " " + tgtLoc.x + ", " + tgtLoc.y + ", " + tgtLoc.z : "");


                botJson.addProperty("elapsedTime", BotUtils.formatTime(bot.getCurrentTask().getElapsedTime()));

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