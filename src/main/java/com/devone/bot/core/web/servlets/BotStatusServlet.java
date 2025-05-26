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
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
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
            BotBlockData tgt = bot.getNavigator().getTarget();

            botJson.addProperty("skin", "http://" + BotWebService.getServerHost() + ":"
                    + BotWebService.getServerPort() + "/skins/" + bot.getUuid() + ".png");

            botJson.addProperty("id", bot.getId());
            botJson.addProperty("name", bot.getNPC().getName());

            botJson.addProperty("stuck", bot.getNavigator().isStuck());
            botJson.addProperty("stucks", bot.getNavigator().getStuckCount());

            botJson.addProperty("position", loc.toCompactString());
            
            String tgtLoc = "";
            if (tgt != null) {
                tgtLoc = tgt.getPosition().toCompactString();
            }
            botJson.addProperty("target", tgtLoc);

            
            BotMemoryV2Partition stats = bot.getBrain().getMemoryV2().partition(BotMemoryPartition.PartitionKey.STATS.toString(), BotMemoryV2Partition.Type.MAP);
            Object teleportUsed = stats.get(BotMemoryItem.ItemKey.TELEPORTED.toString());

            if (teleportUsed != null) {
                botJson.addProperty("teleports", (Number) teleportUsed);
            } else {
                 botJson.addProperty("teleports", (Number) 0);
            }

            // Общее число сломанных блоков
            BotMemoryV2Partition blocks = stats.partition(BotMemoryPartition.PartitionKey.DESTROYED.toString(), BotMemoryV2Partition.Type.MAP);            
            Object totalBlocks = blocks.get(BotMemoryItem.ItemKey.TOTAL.toString());

            if (totalBlocks != null) {
                botJson.addProperty("breaks", (Number) totalBlocks);
            } else {
                botJson.addProperty("breaks", (Number) 0);
            }

            // Общее число убитых мобов
            BotMemoryV2Partition mobs = stats.partition(BotMemoryPartition.PartitionKey.KILLED.toString(), BotMemoryV2Partition.Type.MAP);
            Object totalMobs = mobs.get(BotMemoryItem.ItemKey.TOTAL.toString());


            if (totalMobs != null) {
                botJson.addProperty("kills", (Number) totalMobs);
            } else {
                botJson.addProperty("kills", (Number) 0);
            }

            botJson.addProperty("autoPickUpItems", true); //hardcode

            botJson.addProperty("task", BotUtils.getActiveTaskIcon(bot));
            
            try {
                botJson.addProperty("taskIsReactive", bot.getActiveTask().isReactive());
            } catch (Exception e) {
                botJson.addProperty("taskIsReactive", false);
            }

            botJson.addProperty("object", BotUtils.getObjective(bot));

            long elapsedTime = 0;
            try {
                elapsedTime = bot.getBrain().getCurrentTask().getElapsedTime();
                botJson.addProperty("elapsedTime", BotUtils.formatTime(elapsedTime));
            } catch (Exception ex) {
                // ignore
            }

            botJson.addProperty("queue", bot.getTaskManager().getQueueIcons());

            // 🧠 Новое: вставляем память как объект
            BotMemoryV2 memory = bot.getBrain().getMemoryV2();
            if (memory != null) {
                botJson.add("memory", memory.toJsonObject());
            }

            // 📦 Инвентарь
            ItemStack[] contents = null;
            if (bot.getInventory().getNPCInventory() != null) {
                contents = bot.getInventory().getNPCInventory().getContents();
            }

            JsonArray inventoryArray = new JsonArray();
            if (contents != null) {
                for (ItemStack item : contents) {
                    if (item != null && item.getAmount() > 0) {
                        JsonObject slotObj = new JsonObject();
                        slotObj.addProperty("type", item.getType().toString().toLowerCase());
                        slotObj.addProperty("amount", item.getAmount());
                        inventoryArray.add(slotObj);
                    }
                }

                int count = Arrays.stream(contents)
                        .filter(Objects::nonNull)
                        .mapToInt(ItemStack::getAmount)
                        .sum();


                botJson.addProperty("inventoryCount", count);
                botJson.addProperty("inventoryMax", 36*64);
                botJson.add("inventorySlotsFilled", inventoryArray);


                BotMemoryV2Partition nav = bot.getBrain().getMemoryV2().partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);

                BotMemoryV2Partition visitedPartition = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);

                if (visitedPartition != null) {
                    botJson.addProperty("visitedCount", visitedPartition.getMap().size());
                } else {
                    botJson.addProperty("visitedCount", 0);
                }
            }

            botsArray.add(botJson);
        }

        result.add("bots", botsArray);
        resp.getWriter().write(result.toString());
    }
}
