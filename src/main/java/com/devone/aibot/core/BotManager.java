package com.devone.aibot.core;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.bluemap.BlueMapMarkers;
import com.devone.aibot.utils.bluemap.BlueMapUtils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BotManager {

    private final AIBotPlugin plugin;
    private final Map<String, Bot> botMap = new HashMap<>();
    private final Map<UUID, Bot> selectedBots = new HashMap<>();
    
    public BotManager(AIBotPlugin plugin) {
        this.plugin = plugin;

        // ✅ Теперь вызываем `loadExistingBots()` напрямую в единственном `runTaskLater`
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BotLogger.debug("Загружаем ботов...");

            loadExistingBots(); // ✅ Загружаем ботов из CitizensAPI и `bots.yml`

            BotLogger.debug("✅ Все боты загружены.");
            
        }, 600L);
    }

    public Bot getBot(String name) {
        return botMap.get(name);
    }

    public void addBot(String name, Bot bot) {
        botMap.put(name, bot);
        saveBots(); // ✅ Сохраняем при добавлении
    }

    public void removeBot(String name) {
        Bot bot = getBot(name);
        if (bot != null) {
            bot.despawn();
        }
        botMap.remove(name);
        saveBots(); // ✅ Сохраняем при удалении
    }

    public boolean botExists(String name) {
        return botMap.containsKey(name);
    }

    public Collection<Bot> getAllBots() {
        return botMap.values();
    }

    private void loadExistingBots() {
        BotLogger.debug("🔄 Загружаем существующих NPC ботов...");

        File file = new File(plugin.getDataFolder(), "bots.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc == null || !npc.getName().startsWith("AI_"))
                continue;

            String botName = npc.getName();
            // UUID npcUUID = npc.getUniqueId();

            if (botExists(botName))
                continue;

            // Set defaults
            Location storedLocation = Bot.getFallbackLocation();
            BotGoal goal = BotGoal.IDLE;

            // Загружаем координаты, если они сохранены
            if (config.contains("bots." + botName)) {
                String path = "bots." + botName;

                int x = config.getInt(path + ".x", storedLocation.getBlockX());
                int y = config.getInt(path + ".y", storedLocation.getBlockY());
                int z = config.getInt(path + ".z", storedLocation.getBlockZ());

                storedLocation = new Location(Bukkit.getWorlds().get(0), x, y, z);

                if (config.contains(path + ".goal")) {
                    goal = BotGoal.valueOf(config.getString(path + ".goal"));
                }
            }

            if (!npc.isSpawned()) {
                npc.spawn(storedLocation);
                BotLogger.debug("✅ NPC " + botName + " был заспавнен.");
            }

            Bot bot = new Bot(botName, npc, this);
            // bot.setUuid(npcUUID);
            bot.setGoal(goal);
            botMap.put(botName, bot);

            BotLogger.debug("♻️ Бот " + botName + " загружен с GOAL: " + bot.getCurrentGoal());
        }

        BotLogger.debug("✅ Загружено NPC ботов: " + botMap.size());

        // ✅ После загрузки сразу обновляем маркеры
        BotLogger.debug("✅ Обновляем карту: " );



        BlueMapMarkers bmm = new BlueMapMarkers(this);
        

        //BlueMapUtils.updateBlueMapMarkers(mSet);

    }

    public void saveBots() {

        File file = new File(plugin.getDataFolder(), "bots.yml");
        FileConfiguration config = new YamlConfiguration();

        for (Bot bot : botMap.values()) {
            String path = "bots." + bot.getId();
            config.set(path + ".uuid", bot.getUuid().toString());

            Location loc = bot.getNPCCurrentLocation();

            // Сохраняем координаты в bots.yml
            config.set("bots." + bot.getId() + ".x", loc.getBlockX());
            config.set("bots." + bot.getId() + ".y", loc.getBlockY());
            config.set("bots." + bot.getId() + ".z", loc.getBlockZ());

            if (bot.getCurrentGoal() != null) {
                config.set(path + ".goal", bot.getCurrentGoal().name());
            }
        }

        try {
            config.save(file);

            BotLogger.debug("✅ Боты сохранены в bots.yml.");

        } catch (IOException e) {
            BotLogger.debug("⚠️ Ошибка сохранения bots.yml: " + e.getMessage());
        }
    }

    public void clearAllBots() {
        botMap.clear();
        BotLogger.debug("🗑 Все боты удалены.");
        saveBots();
    }

    public void despawnBots() {
        for (Bot bot : botMap.values()) {
            bot.despawn();
        }
        botMap.clear();
        BotLogger.debug("🗑 Все боты desawned].");
        saveBots();
    }

    public void selectBot(UUID playerUUID, Bot bot) {
        selectedBots.put(playerUUID, bot);
    }

    public Bot getOrSelectBot(UUID playerId) {
        Bot bot = getSelectedBot(playerId);

        // ✅ Если бот не выбран, но есть только один, выбираем его
        if (bot == null && botMap.size() == 1) {
            bot = botMap.values().iterator().next();
            selectBot(playerId, bot);
        }

        return bot;
    }

    public Bot getSelectedBot(UUID playerUUID) {
        return selectedBots.get(playerUUID);
    }

    public AIBotPlugin getPlugin(){
        return plugin;
    }
}
