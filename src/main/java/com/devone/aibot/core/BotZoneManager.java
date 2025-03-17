package com.devone.aibot.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotLogger;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BotZoneManager {

    public static BotZoneManager instance; // ✅ Статическая ссылка на текущий экземпляр

    private final AIBotPlugin plugin;
    private final File zonesFile;
    private final FileConfiguration config;
    private final Map<String, BotProtectedZone> protectedZones = new HashMap<>();
    private static final long AUTO_SAVE_INTERVAL = 5 * 60 * 20; // 5 minutes in ticks

    public BotZoneManager(AIBotPlugin plugin, File pluginFolder) {
        this.plugin = plugin;
        instance = this; // ✅ Сохраняем экземпляр для статического доступа

        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        zonesFile = new File(BotConstants.PLUGIN_PATH_CONFIGS, "zones.yml");
        config = YamlConfiguration.loadConfiguration(zonesFile);
        loadZones();

        // ✅ Schedule auto-save every 5 minutes
        scheduleAutoSave();
    }

    private void loadZones() {
        protectedZones.clear();
        if (config.contains("zones")) {
            for (String zoneName : config.getConfigurationSection("zones").getKeys(false)) {
                double x = config.getDouble("zones." + zoneName + ".x");
                double y = config.getDouble("zones." + zoneName + ".y");
                double z = config.getDouble("zones." + zoneName + ".z");
                int radius = config.getInt("zones." + zoneName + ".radius");
                protectedZones.put(zoneName, new BotProtectedZone(x, y, z, radius));
                BotLogger.info("━ Loaded zone: " + zoneName + " at (" + x + ", " + y + ", " + z + ") with radius " + radius);
            }
        }
        BotLogger.info("☰ Total zones loaded: " + protectedZones.size());
    }

    public void saveZones() {
        config.set("zones", null);
        for (Map.Entry<String, BotProtectedZone> entry : protectedZones.entrySet()) {
            String zoneName = entry.getKey();
            BotProtectedZone zone = entry.getValue();
            config.set("zones." + zoneName + ".x", (int) Math.round(zone.getX()));
            config.set("zones." + zoneName + ".y", (int) Math.round(zone.getY()));
            config.set("zones." + zoneName + ".z", (int) Math.round(zone.getZ()));
            config.set("zones." + zoneName + ".radius", zone.getRadius());
        }
        try {
            config.save(zonesFile);
            BotLogger.info("🗺️ Zones saved succesfully.");

        } catch (IOException e) {
            BotLogger.error("❌ Failed to save zones: " + e.getMessage());
        }
    }    

    public void addZone(String name, Location center, int radius) {
        protectedZones.put(name, new BotProtectedZone(center.getX(), center.getY(), center.getZ(), radius));
        BotLogger.info("➕ Added new zone: " + name + " at " + center.toString() + " with radius " + radius);
    }

    public boolean removeZone(String name) {
        if (protectedZones.remove(name) != null) {
            BotLogger.info("➖ Removed zone: " + name);
            return true;
        }
        return false;
    }

    public boolean isInProtectedZone(Location location) {
        for (BotProtectedZone zone : protectedZones.values()) {
            if (zone.isInside(location)) {
                return true;
            }
        }
        return false;
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

    // ✅ Статический метод для проверки зоны
    public static boolean isLocationInProtectedZone(Location location) {
        if (instance == null) {
            BotLogger.error("❌ ZoneManager не инициализирован! Невозможно проверить зону.");
            return false;
        }
        return instance.isInProtectedZone(location);
    }

    // ✅ Schedule periodic auto-save every 5 minutes
    private void scheduleAutoSave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveZones, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    public static BotZoneManager getInstance() {
        return AIBotPlugin.getInstance().getZoneManager();
    }
    
}
