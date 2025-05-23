package com.devone.bot.core.storage;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memoryv2.BotMemoryV2;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;
import com.google.gson.*;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.io.*;

public class BotDataStorage {

    private static final File BOT_DATA_DIR = new File(BotConstants.PLUGIN_PATH_CONFIGS_BOTS);

    static {
        BOT_DATA_DIR.mkdirs();
    }

    public static void saveBotData(Bot bot) {
        String name = bot.getId();
        File baseFile = new File(BOT_DATA_DIR, name);

        // 🧠 Save memory
        try {
            File memoryFile = new File(baseFile + ".memory.json");
            bot.getBrain().getMemoryV2().saveToFile(memoryFile);
        } catch (IOException e) {
            BotLogger.warn("🧠", true, name + " — failed to save memory: " + e.getMessage());
        }

        // 🎒 Save inventory
        try {
            File inventoryFile = new File(baseFile + ".inventory.json");
            saveInventory(bot, inventoryFile);
        } catch (IOException e) {
            BotLogger.warn("🎒", true, name + " — failed to save inventory: " + e.getMessage());
        }

        // 📎 (Optional: save snapshot, etc.)
    }

    public static void loadBotData(Bot bot) {
        String name = bot.getId();
        File baseFile = new File(BOT_DATA_DIR, name);

        // 🧠 Load memory
        File memoryFile = new File(baseFile + ".memory.json");
        if (memoryFile.exists()) {
            try {
                BotMemoryV2 memory = BotMemoryV2.loadFromFile(memoryFile);
                bot.getBrain().setMemoryV2(memory);
            } catch (IOException e) {
                BotLogger.warn("🧠", true, name + " — failed to load memory: " + e.getMessage());
            }
        }

        // 🎒 Load inventory
        File inventoryFile = new File(baseFile + ".inventory.json");
        if (inventoryFile.exists()) {
            try {
                loadInventory(bot, inventoryFile);
            } catch (IOException e) {
                BotLogger.warn("🎒", true, name + " — failed to load inventory: " + e.getMessage());
            }
        }
    }

    public static void deleteBotData(String botId) {
        File[] files = BOT_DATA_DIR.listFiles((dir, name) -> name.startsWith(botId + ".") && name.endsWith(".json"));
        if (files != null) {
            for (File f : files) f.delete();
        }
    }

    private static void saveInventory(Bot bot, File file) throws IOException {
        JsonArray items = new JsonArray();
        Inventory inventory = bot.getInventory().getNPCInventory();
        if(inventory==null) return;
        
        ItemStack[] contents = bot.getInventory().getNPCInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && item.getAmount() > 0) {
                JsonObject obj = new JsonObject();
                obj.addProperty("type", item.getType().toString());
                obj.addProperty("amount", item.getAmount());
                items.add(obj);
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(items.toString());
        }
    }

    private static void loadInventory(Bot bot, File file) throws IOException {
        JsonArray items;
        try (FileReader reader = new FileReader(file)) {
            items = JsonParser.parseReader(reader).getAsJsonArray();
        }

        ItemStack[] contents = new ItemStack[36];
        int slot = 0;

        for (JsonElement el : items) {
            JsonObject obj = el.getAsJsonObject();
            Material mat = Material.getMaterial(obj.get("type").getAsString());
            int amt = obj.get("amount").getAsInt();
            if (mat != null && amt > 0 && slot < contents.length) {
                contents[slot++] = new ItemStack(mat, amt);
            }
        }

        bot.getInventory().getNPCInventory().setContents(contents);
    }
}
