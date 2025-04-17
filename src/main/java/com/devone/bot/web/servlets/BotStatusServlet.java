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
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotLocation;
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
            BotLocation loc = bot.getBrain().getCurrentLocation();
            if (loc != null) {
                botJson.addProperty("skin", "http://" + BotWebService.getServerHost() + ":"+BotWebService.getServerPort()+"/skins/" + bot.getUuid() + ".png");
                
                botJson.addProperty("id", bot.getId());
                botJson.addProperty("name", bot.getNPC().getName());

                botJson.addProperty("stuck", bot.getBrain().isStuck());
                botJson.addProperty("stuckCount", bot.getBrain().getStuckCount());

                botJson.addProperty("blocks_broken_size", bot.getBrain().getBlocksBroken().size());
                botJson.addProperty("blocks_broken",  bot.getBrain().getBlocksBroken().toString());
                botJson.addProperty("mobs_killed_size", bot.getBrain().getMobsKilled().size());
                botJson.addProperty("mobs_killed", bot.getBrain().getMobsKilled().toString());
                botJson.addProperty("teleport_used", bot.getBrain().getTeleportUsed());
                botJson.addProperty("auto_pick_up_items", bot.getBrain().getAutoPickupItems());

                String currLoc = " " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();   

                botJson.addProperty("position", currLoc);

                botJson.addProperty("task", bot.getBrain().getCurrentTask().getIcon());
                
                botJson.addProperty("object", getCurrentObjective(bot));

                BotLocation tgtLoc = bot.getBrain().getTargetLocation();

                botJson.addProperty("target", tgtLoc != null ? " " + tgtLoc.getX() + ", " + tgtLoc.getY() + ", " + tgtLoc.getZ() : "");


                botJson.addProperty("elapsedTime", BotUtils.formatTime(bot.getBrain().getCurrentTask().getElapsedTime()));

                List<BotTask<?>> taskStack = (bot.getLifeCycle() != null && bot.getLifeCycle().getTaskStackManager() != null)
                    ? new ArrayList<>(bot.getLifeCycle().getTaskStackManager().getTaskStack())
                    : new ArrayList<>();
                String taskStackText = taskStack.isEmpty() ? "N/A" :
                    taskStack.stream().map(BotTask::getIcon).collect(Collectors.joining(" ➜ "));
                botJson.addProperty("queue", taskStackText);
                botsArray.add(botJson);


                // 📦 Serialize inventory
                ItemStack[] contents = bot.getInventory().getNPCInventory().getContents();
                if (contents == null) {
                    continue;
                }
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
        BotTask<?> currentTask = bot.getBrain().getCurrentTask();
        return (currentTask != null) ? currentTask.getObjective() : "";
    }
}