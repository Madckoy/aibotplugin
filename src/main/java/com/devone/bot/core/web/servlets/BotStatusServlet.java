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
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotPosition;
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

            BotPosition loc = bot.getNavigator().getPosition();
            BotPosition tgt = bot.getNavigator().getPoi();

            if (loc != null) {
                botJson.addProperty("skin", "http://" + BotWebService.getServerHost() + ":"
                        + BotWebService.getServerPort() + "/skins/" + bot.getUuid() + ".png");

                botJson.addProperty("id", bot.getId());
                botJson.addProperty("name", bot.getNPC().getName());

                botJson.addProperty("stuck", bot.getNavigator().isStuck());
                botJson.addProperty("stuckCount", bot.getNavigator().getStuckCount());

                botJson.addProperty("blocksBroken", bot.getBrain().getMemory().getBlocksBroken());
                botJson.addProperty("mobsKilled", bot.getBrain().getMemory().getMobsKilled());
                botJson.addProperty("teleportUsed", bot.getBrain().getMemory().getTeleportUsed());

                botJson.addProperty("autoPickUpItems", bot.getBrain().getAutoPickupItems());

                String currLoc = loc.getX() + ", " + loc.getY() + ", " + loc.getZ();

                botJson.addProperty("position", currLoc);

                botJson.addProperty("task", BotUtils.getActiveTaskIcon(bot));
                botJson.addProperty("taskIsReactive", BotUtils.getActiveTaskIcon(bot));

                botJson.addProperty("object", BotUtils.getObjective(bot));

                String tgtLoc = "";

                if (tgt != null) {
                    tgtLoc = tgt.getX() + ", " + tgt.getY() + ", " + tgt.getZ();
                }

                botJson.addProperty("target", tgtLoc);

                long elapsedTime = 0;

                try {
                    elapsedTime = bot.getBrain().getCurrentTask().getElapsedTime();
                    botJson.addProperty("elapsedTime", BotUtils.formatTime(elapsedTime));
                } catch (Exception ex) {

                }

                botJson.addProperty("queue", bot.getTaskManager().getQueueIcons());

                botJson.addProperty("memory", bot.getBrain().getMemory().toJson().toString());

                // add navigation data
                botJson.addProperty("navigationSuggestion", BotUtils.getNavigationSuggestionAsString(bot));

                botJson.addProperty("reachableTargets",
                        bot.getNavigator().getNavigationSummaryItem("poi").toString());
                botJson.addProperty("reachableBlocks",
                        bot.getNavigator().getNavigationSummaryItem("reachable").toString());
                botJson.addProperty("navigableBlocks",
                        bot.getNavigator().getNavigationSummaryItem("navigable").toString());
                botJson.addProperty("walkableBlocks",
                        bot.getNavigator().getNavigationSummaryItem("walkable").toString());

                Object obj = bot.getNavigator().getSuggestedPoi();
                if (obj != null) {
                    botJson.addProperty("suggestedBlock",
                            bot.getNavigator().getSuggestedPoi().toString());
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

}