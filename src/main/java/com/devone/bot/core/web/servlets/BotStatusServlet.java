package com.devone.bot.core.web.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.brain.logic.navigator.BotNavigator;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.web.BotWebService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BotStatusServlet extends HttpServlet {
    private final BotManager botManager;

    public BotStatusServlet(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        JsonObject result = new JsonObject();

        String serverTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        result.addProperty("server-time", serverTime);

        // Время Minecraft
        long mcTicks = Bukkit.getWorlds().get(0).getTime();
        int hour = (int) ((mcTicks / 1000 + 6) % 24);
        int minute = (int) ((mcTicks % 1000) * 60 / 1000);
        String mcTimeFormatted = String.format("%02d:%02d", hour, minute);
        result.addProperty("mc-time", mcTimeFormatted);

        JsonArray botsArray = new JsonArray();
        Collection<Bot> bots = botManager.getAllBots();

        for (Bot bot : bots) {
            JsonObject botJson = new JsonObject();

            BotLocation loc = bot.getNavigation().getLocation();
            BotLocation tgt = bot.getNavigation().getTarget();

            if (loc != null) {
                botJson.addProperty("skin", "http://" + BotWebService.getServerHost() + ":"
                        + BotWebService.getServerPort() + "/skins/" + bot.getUuid() + ".png");

                botJson.addProperty("id", bot.getId());
                botJson.addProperty("name", bot.getNPC().getName());

                botJson.addProperty("stuck", bot.getNavigation().isStuck());
                botJson.addProperty("stuckCount", bot.getNavigation().getStuckCount());

                botJson.addProperty("blocksBroken", bot.getBrain().getMemory().getBlocksBroken());
                botJson.addProperty("mobsKilled", bot.getBrain().getMemory().getMobsKilled());
                botJson.addProperty("teleportUsed", bot.getBrain().getMemory().getTeleportUsed());

                botJson.addProperty("autoPickUpItems", bot.getBrain().getAutoPickupItems());

                String currLoc = loc.getX() + ", " + loc.getY() + ", " + loc.getZ();

                botJson.addProperty("position", currLoc);

                botJson.addProperty("task", bot.getBrain().getCurrentTask().getIcon());
                botJson.addProperty("taskIsReactive", bot.getBrain().getCurrentTask().isReactive());

                botJson.addProperty("object", getCurrentObjective(bot));

                String tgtLoc = "";
                
                if(tgt!=null) {
                    tgtLoc = tgt.getX() + ", " + tgt.getY() + ", " + tgt.getZ();
                }

                botJson.addProperty("target", tgtLoc );

                botJson.addProperty("elapsedTime",
                        BotUtils.formatTime(bot.getBrain().getCurrentTask().getElapsedTime()));

                botJson.addProperty("queue", bot.getTaskManager().getQueueIcons());

                botJson.addProperty("memory", bot.getBrain().getMemory().toJson().toString());

                // add navigation data
                if (bot.getNavigation().getSuggestion() == BotNavigator.NavigationType.TELEPORT) {
                    botJson.addProperty("navigationSuggestion", "Teleport");
                } else {
                    botJson.addProperty("navigationSuggestion", "Walk");
                }

                botJson.addProperty("reachableTargets",
                        bot.getNavigation().getNavigationSummaryItem("targets").toString());
                botJson.addProperty("reachableBlocks",
                        bot.getNavigation().getNavigationSummaryItem("reachable").toString());
                botJson.addProperty("navigableBlocks",
                        bot.getNavigation().getNavigationSummaryItem("navigable").toString());
                botJson.addProperty("walkableBlocks",
                        bot.getNavigation().getNavigationSummaryItem("walkable").toString());
                
                Object obj = bot.getNavigation().getSuggested();
                if(obj!=null) {
                    botJson.addProperty("suggestedBlock",
                        bot.getNavigation().getSuggested().getLocation().toString()+ " | " + bot.getNavigation().getSuggested().getType() );
                }
                    
                botsArray.add(botJson);

                ItemStack[] contents = null;

                if (bot.getInventory().getNPCInventory() != null) {
                    // 📦 Serialize inventory
                    contents = bot.getInventory().getNPCInventory().getContents();
                }

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