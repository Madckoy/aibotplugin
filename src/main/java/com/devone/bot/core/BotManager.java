package com.devone.bot.core;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.config.BotManagerConfig;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.devone.bot.core.web.bluemap.BlueMapMarkers;

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
            BotLogger.debug("🤖", true, "💡Loading bots...");
            loadExistingBots();
            BotLogger.debug("🤖", true, "✅ All bots loaded.");
        }, 600L);
    }

    private void loadExistingBots() {
        botsMap.clear();
        BotManagerConfig.Data loadedData = config.loadOrCreate();

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc == null || !npc.getName().startsWith("AI_"))
                continue;

            String botName = npc.getName();
            UUID npcUUID = npc.getUniqueId();
            
            // Доп. защита — сравнение UUID, если вдруг одинаковые имена
            boolean alreadyRegistered = botsMap.values().stream()
                    .anyMatch(b -> b.getNPCEntity().getUniqueId().equals(npcUUID));
            
            if (botExists(botName) || alreadyRegistered) {
                BotLogger.warn("🤖", true, "🚨 Duplicate or already registered NPC: " + botName + " (UUID: " + npcUUID + ")");
                continue;
            }
            

            BotLocation storedLocation = BotUtils.getFallbackLocation();

            if (loadedData.bots.containsKey(botName)) {
                BotLocation coord = loadedData.bots.get(botName).position;
                storedLocation = coord;
            }

            if (!npc.isSpawned()) {
                Location spawnLocation = BotWorldHelper.getWorldLocation(storedLocation);
                npc.spawn(spawnLocation);
                BotLogger.debug("🤖", true, "✅ Spawned NPC: " + botName);
            }

            Bot bot = new Bot(botName, npc, this);
            botsMap.put(botName, bot);
            BotLogger.debug("🤖", true, bot.getId() + " ✅ Added to the map!");
        }

        bm_markers = new BlueMapMarkers(this);
        bm_markers.scheduleMarkerUpdate();

        BotLogger.debug("🤖" , true, "✅ Loaded NPC bots: " + botsMap.size());
    }

    public void saveBots() {
        BotManagerConfig.Data data = config.get();
        data.bots.clear();

        botsMap.forEach((name, bot) -> {

            data.bots.put(name, new BotManagerConfig.BotEntry(bot));

        });

        config.save();
        BotLogger.debug("🤖" , true, "✅ Bots saved to bots.json.");
    }

    public void addBot(String name, Bot bot) {
        if (botsMap.containsKey(name)) {
            BotLogger.warn("🤖", true, "🚨 Bot already registered: " + name + ", skipping.");
            return;
        }
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
            BotLogger.debug("🤖", true,  name + "➖ has been removed.");
        }
    }

    public void removeAllBots() {
        new ArrayList<>(botsMap.keySet()).forEach(this::removeBot);
        BotLogger.debug("🤖", true, "✅ All bots removed.");
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
