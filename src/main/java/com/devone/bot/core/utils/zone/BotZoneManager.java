package com.devone.bot.core.utils.zone;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.devone.bot.core.bot.blocks.BotLocation;
import com.devone.bot.core.bot.config.BotZoneConfig;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.plugin.AIBotPlugin;

import java.io.File;
import java.util.*;

public class BotZoneManager {

    private static BotZoneManager instance;

    private final AIBotPlugin plugin;
    private final BotZoneConfig config;
    private final Map<String, BotProtectedZone> protectedZones = new HashMap<>();
    private static final long AUTO_SAVE_INTERVAL = 5 * 60 * 20;

    public BotZoneManager(AIBotPlugin plugin, File pluginFolder) {
        this.plugin = plugin;
        instance = this;

        File zonesFile = new File(BotConstants.PLUGIN_PATH_CONFIGS, BotZoneManager.class.getSimpleName() + ".json");
        this.config = new BotZoneConfig(zonesFile);
        loadZones();

        scheduleAutoSave();
    }

    private void loadZones() {
        protectedZones.clear();
        BotZoneConfig.Data loadedData = config.loadOrCreate();
        for (Map.Entry<String, BotZoneConfig.ZoneEntry> entry : loadedData.zones.entrySet()) {
            String zoneName = entry.getKey();
            BotZoneConfig.ZoneEntry z = entry.getValue();
            protectedZones.put(zoneName, new BotProtectedZone(z.x, z.y, z.z, z.radius));
            BotLogger.debug("━",true, "Loaded zone: " + zoneName + " at (" + z.x + ", " + z.y + ", " + z.z + ") with radius " + z.radius);
        }
        BotLogger.debug("☰", true, "Total zones loaded: " + protectedZones.size());
    }

    public void saveZones() {
        BotZoneConfig.Data data = config.get();
        data.zones.clear();
        protectedZones.forEach((name, zone) ->
            data.zones.put(name, new BotZoneConfig.ZoneEntry(zone.getX(), zone.getY(), zone.getZ(), zone.getRadius()))
        );
        config.save();
        BotLogger.debug("🗺️", true, "Zones saved successfully.");
    }

    public void addZone(String name, Location center, int radius) {
        protectedZones.put(name, new BotProtectedZone(center.getX(), center.getY(), center.getZ(), radius));
        BotLogger.debug("➕", true, "Added new zone: " + name + " at " + center + " with radius " + radius);
    }

    public boolean removeZone(String name) {
        if (protectedZones.remove(name) != null) {
            BotLogger.debug("➖", true, "Removed zone: " + name);
            return true;
        }
        return false;
    }

    public boolean isInProtectedZone(BotLocation location) {
        return protectedZones.values().stream().anyMatch(z -> z.isInside(location));
    }

    public Set<String> listZones() {
        return protectedZones.keySet();
    }

    public int getZoneRadius(String zoneName) {
        BotProtectedZone zone = protectedZones.get(zoneName);
        return (zone != null) ? zone.getRadius() : -1;
    }

    public BotProtectedZone getZone(String zoneName) {
        return protectedZones.get(zoneName);
    }

    public static boolean isLocationInProtectedZone(BotLocation location) {
        if (instance == null) {
            BotLogger.debug("❌",true, "ZoneManager not initialized!");
            return false;
        }
        return instance.isInProtectedZone(location);
    }

    private void scheduleAutoSave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveZones, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    public static BotZoneManager getInstance() {
        return instance;
    }
}
