
package com.devone.bot.core.web.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2Partition;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.blocks.BotPositionKey;
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
        result.addProperty("server_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        long mcTicks = Bukkit.getWorlds().get(0).getTime();
        result.addProperty("mc_time", String.format("%02d:%02d", (mcTicks / 1000 + 6) % 24, (mcTicks % 1000) * 60 / 1000));

        JsonArray botsArray = new JsonArray();
        for (Bot bot : botManager.getAllBots()) {
            botsArray.add(buildBotJson(bot));
        }
        result.add("bots", botsArray);
        resp.getWriter().write(result.toString());
    }

    private JsonObject buildBotJson(Bot bot) {
        JsonObject botJson = new JsonObject();
        BotPosition loc = bot.getNavigator().getPosition();
        BotBlockData tgt = bot.getNavigator().getTarget();

        botJson.addProperty("skin", "http://" + BotWebService.getServerHost() + ":" + BotWebService.getServerPort() + "/skins/" + bot.getUuid() + ".png");
        botJson.addProperty("id", bot.getId());
        botJson.addProperty("name", bot.getNPC().getName());
        botJson.addProperty("stuck", bot.getNavigator().isStuck());
        botJson.addProperty("stucks", bot.getNavigator().getStuckCount());
        botJson.addProperty("position", loc.toCompactString());
        botJson.addProperty("target", tgt != null ? tgt.getPosition().toCompactString() : "");
        botJson.addProperty("auto_pick_up_items", true);
        botJson.addProperty("task", BotUtils.getActiveTaskIcon(bot));

        try { botJson.addProperty("task_is_reactive", bot.getActiveTask().isReactive()); } catch (Exception e) { botJson.addProperty("task_is_reactive", false); }
        botJson.addProperty("object", BotUtils.getObjective(bot));

        try { botJson.addProperty("elapsed_time", BotUtils.formatTime(bot.getBrain().getCurrentTask().getElapsedTime())); } catch (Exception e) {}

        botJson.addProperty("queue", bot.getTaskManager().getQueueIcons());

        BotMemoryV2 memory = bot.getBrain().getMemoryV2();
        if (memory != null) {
            botJson.add("memory", memory.toJsonObject());
        }

        addStatsJson(bot, botJson);
        addInventoryJson(bot, botJson);
        addWatchdogJson(bot, botJson);
        addVisitedJson(bot, botJson);
        return botJson;
    }

    private void addStatsJson(Bot bot, JsonObject json) {
        BotMemoryV2Partition stats = bot.getBrain().getMemoryV2().partition(BotMemoryPartition.PartitionKey.STATS.toString(), BotMemoryV2Partition.Type.MAP);
        json.addProperty("teleports", (Number) Objects.requireNonNullElse(stats.get(BotMemoryItem.ItemKey.TELEPORTED.toString()), 0));

        BotMemoryV2Partition blocks = stats.partition(BotMemoryPartition.PartitionKey.DESTROYED.toString(), BotMemoryV2Partition.Type.MAP);
        json.addProperty("breaks", (Number) Objects.requireNonNullElse(blocks.get(BotMemoryItem.ItemKey.TOTAL.toString()), 0));

        BotMemoryV2Partition mobs = stats.partition(BotMemoryPartition.PartitionKey.KILLED.toString(), BotMemoryV2Partition.Type.MAP);
        json.addProperty("kills", (Number) Objects.requireNonNullElse(mobs.get(BotMemoryItem.ItemKey.TOTAL.toString()), 0));
    }

    private void addInventoryJson(Bot bot, JsonObject json) {
        ItemStack[] contents = bot.getInventory().getNPCInventory() != null ? bot.getInventory().getNPCInventory().getContents() : new ItemStack[0];
        JsonArray inventoryArray = new JsonArray();

        int count = 0;
        for (ItemStack item : contents) {
            if (item != null && item.getAmount() > 0) {
                JsonObject slot = new JsonObject();
                slot.addProperty("type", item.getType().toString().toLowerCase());
                slot.addProperty("amount", item.getAmount());
                inventoryArray.add(slot);
                count += item.getAmount();
            }
        }

        json.addProperty("inventory_count", count);
        json.addProperty("inventory_max", 36 * 64);
        json.add("inventory_slots_filled", inventoryArray);
    }

    private void addVisitedJson(Bot bot, JsonObject json) {
        BotMemoryV2Partition nav = bot.getBrain().getMemoryV2().partition(BotMemoryPartition.PartitionKey.NAVIGATION.toString(), BotMemoryV2Partition.Type.MAP);
        BotMemoryV2Partition visited = nav.partition(BotMemoryPartition.PartitionKey.VISITED.toString(), BotMemoryV2Partition.Type.MAP);
        json.addProperty("visited_count", visited != null ? visited.getMap().size() : 0);
    }

    private void addWatchdogJson(Bot bot, JsonObject json) {
        //BotPosition lastPos = new BotPosition(bot.getNavigator().getPosition());//BotMemoryV2Utils.readValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.POSITION, BotPosition.class);
        BotPositionKey lastPos = (BotPositionKey) BotMemoryV2Utils.readValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.POSITION_KEY, BotPositionKey.class);        
        Long lastTime = BotMemoryV2Utils.readValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.TIME, Long.class);
        Long  remaining = BotMemoryV2Utils.readValueTyped(bot, BotMemoryPartition.PartitionKey.WATCHDOG, BotMemoryItem.ItemKey.REMAINING_TIME, Long.class);

        if (lastPos != null) json.addProperty("watchdog_pos", lastPos.toString());
        if (lastTime != null) json.addProperty("watchdog_time", lastTime);
        if (remaining != null) json.addProperty("watchdog_remaining", remaining);
    }
}
