package com.devone.aibot.core;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.config.BotManagerConfig;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotCoordinate3D;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.bluemap.BlueMapMarkers;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.*;

public class BotManager {

    private final AIBotPlugin plugin;
    private final Map<String, Bot> botsMap = new HashMap<>();
    private final Map<UUID, Bot> selectedBots = new HashMap<>();
    private final BotManagerConfig config;
    private BlueMapMarkers bm_markers;

    public BotManager(AIBotPlugin plugin) {
        this.plugin = plugin;

        File botsFile = new File(BotConstants.PLUGIN_PATH_CONFIGS, BotManagerConfig.class.getSimpleName() + ".json");
        this.config = new BotManagerConfig(botsFile);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BotLogger.info(true, "💡 Loading bots...");
            loadExistingBots();
            BotLogger.info(true, "✅ All bots loaded.");
        }, 600L);
    }

    private void loadExistingBots() {
        botsMap.clear();
        BotManagerConfig.Data loadedData = config.loadOrCreate();

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc == null || !npc.getName().startsWith("AI_"))
                continue;

            String botName = npc.getName();

            if (botExists(botName))
                continue;

            Location storedLocation = BotUtils.getFallbackLocation();

            if (loadedData.bots.containsKey(botName)) {
                BotCoordinate3D coord = loadedData.bots.get(botName).position;
                storedLocation = new Location(Bukkit.getWorlds().get(0), coord.x, coord.y, coord.z);
            }

            if (!npc.isSpawned()) {
                npc.spawn(storedLocation);
                BotLogger.info(true, "✅ Spawned NPC: " + botName);
            }

            Bot bot = new Bot(botName, npc, this);
            botsMap.put(botName, bot);
            BotLogger.info(true, "✅ " + bot.getId() + " added to the map!");
        }

        bm_markers = new BlueMapMarkers(this);
        bm_markers.scheduleMarkerUpdate();

        BotLogger.info(true, "✅ Loaded NPC bots: " + botsMap.size());
    }

    public void saveBots() {
        BotManagerConfig.Data data = config.get();
        data.bots.clear();

        botsMap.forEach((name, bot) -> {
            Location loc = bot.getRuntimeStatus().getCurrentLocation();
            BotCoordinate3D coord = new BotCoordinate3D(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            data.bots.put(name, new BotManagerConfig.BotEntry(bot.getUuid().toString(), coord));
        });

        config.save();
        BotLogger.info(true, "✅ Bots saved to bots.json.");
    }

    public void addBot(String name, Bot bot) {
        botsMap.put(name, bot);
        saveBots();
        bm_markers.scheduleMarkerUpdate();
    }

    public void removeBot(String name) {
        Bot bot = getBot(name);
        if (bot != null) {
            bot.despawnNPC();
            botsMap.remove(name);
            bm_markers.removeMarker(name);
            saveBots();
            BotLogger.info(true, "➖ " + name + " has been removed.");
        }
    }

    public void removeAllBots() {
        new ArrayList<>(botsMap.keySet()).forEach(this::removeBot);
        BotLogger.info(true, "✅ All bots removed.");
    }

    public Bot getBot(String name) {
        return botsMap.get(name);
    }

    public boolean botExists(String name) {
        return botsMap.containsKey(name);
    }

    public Collection<Bot> getAllBots() {
        return botsMap.values();
    }

    public void selectBot(UUID playerUUID, Bot bot) {
        selectedBots.put(playerUUID, bot);
    }

    public boolean unselectBot(UUID playerUUID) {
        return selectedBots.remove(playerUUID) != null;
    }

    public Bot getOrSelectBot(UUID playerId) {
        Bot bot = getSelectedBot(playerId);
        if (bot == null && botsMap.size() == 1) {
            bot = botsMap.values().iterator().next();
            selectBot(playerId, bot);
        }
        return bot;
    }

    public Bot getSelectedBot(UUID playerUUID) {
        return selectedBots.get(playerUUID);
    }

    public boolean isBot(Entity entity) {
        return botsMap.values().stream()
                .filter(bot -> bot.getNPCEntity() != null)
                .anyMatch(bot -> bot.getNPCEntity().getUniqueId().equals(entity.getUniqueId()));
    }

    public AIBotPlugin getPlugin() {
        return plugin;
    }
}
