package com.devone.aibot.utils.bluemap;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotMovementLogger;

import de.bluecolored.bluemap.api.markers.POIMarker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueMapMarkers {
    private final BotManager botManager;
    private final File markersFile;

    private final Map<String, Location> lastKnownLocations = new HashMap<>();

    public BlueMapMarkers(BotManager botManager) {
        this.botManager = botManager;
        this.markersFile = new File("plugins/dynmap/markers.yml");

        if (!markersFile.exists()) {
            try {
                markersFile.createNewFile();
                BotLogger.debug("[DynmapBotMarkers] markers.yml не найден, создан новый файл.");
            } catch (IOException e) {
                BotLogger.error("[DynmapBotMarkers] Ошибка создания markers.yml: " + e.getMessage());
            }
        }
    }

    public void updateAllMarkers() {
        if (!markersFile.exists()) {
            BotLogger.error("[DynmapBotMarkers] ❌ markers.yml отсутствует! Перезапустите сервер или создайте файл вручную.");
            return;
        }
    
        YamlConfiguration config = YamlConfiguration.loadConfiguration(markersFile);
    
        if (!config.contains("sets.bots")) {
            config.set("sets.bots.hide", false);
            config.set("sets.bots.layerprio", 10);
            config.set("sets.bots.minzoom", 0);
            config.set("sets.bots.showlabels", true);
            config.set("sets.bots.label", "Bots");
            config.createSection("sets.bots.markers");
        }
    
        boolean hasChanges = false;
        List<Bot> bots = List.copyOf(botManager.getAllBots());
    
        if (bots.isEmpty()) {
            BotLogger.debug("[DynmapBotMarkers] ⚠ Нет ботов, markers.yml не обновляется.");
            return;
        }
    
        for (Bot bot : bots) {

            Location loc = bot.getNPCCurrentLocation(); 

            if (loc != null) {
                String botId = bot.getId();
                Location lastLocation = lastKnownLocations.get(botId);

                if( lastLocation!=null ) {
                    BotLogger.debug("[DynmapBotMarkers]" + bot.getId() +" 📍Last known location on map: " +
                            " X:" + lastLocation.getBlockX() + " Y:" + lastLocation.getBlockY() + " Z:" + lastLocation.getBlockZ());
                    

                    // Если позиция не изменилась – пропускаем обновление
                    if (lastLocation.equals(loc)) {
                        BotLogger.debug("[DynmapBotMarkers]" + bot.getId() +" 📍 Locations are the same! ");
                        continue; 
                    }
                }
    
                // Обновляем маркер
                String path = "sets.bots.markers." + botId;
                config.set(path + ".world", loc.getWorld().getName());
                config.set(path + ".x", loc.getBlockX());
                config.set(path + ".y", loc.getBlockY()); 
                config.set(path + ".z", loc.getBlockZ());
                config.set(path + ".label", botId + " ("+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ()+")");
                config.set(path + ".icon","default/robot");
    
                lastKnownLocations.put(botId, loc.clone()); // Обновляем кешированное значение

                hasChanges = true; 
                
                BotMovementLogger.logBotMovement(bot);
    
                BotLogger.debug("[DynmapBotMarkers]" + bot.getId() +" 📍 Обновлён маркер бота : " +
                        " X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());

            } else {
                BotLogger.debug("[DynmapBotMarkers]" + bot.getId() + " 📍 ALL Bot Locations are unknown. Skip update.");
            }
           
        }
    
        if (hasChanges) { 
            try {
                config.save(markersFile);
                BotLogger.debug("[BlueMapMarkers] ✅ markers.yml обновлён!");


                // 🔥 Форсируем обновление карты ОДИН раз, после завершения цикла
                Bukkit.getScheduler().runTaskLater(botManager.getPlugin(), () -> {
                    BotLogger.info("[DynmapBotMarkers] 🔄 Форсируем обновление карты!");

                    BlueMapUtils.updateBlueMapMarkers(lastKnownLocations);
   
   
            }, 20L); // 1-секундная задержка, чтобы не грузить сервер


            } catch (IOException e) {
                BotLogger.error("[DynmapBotMarkers] ❌ Ошибка при сохранении marker: " + e.getMessage());
            }
        }
    }

    public void scheduleMarkerUpdate() {

        Bukkit.getScheduler().runTaskTimer(botManager.getPlugin(), () -> {
            BotLogger.debug("[DynmapBotMarkers] ✅ Обновление маркеров запущено.");
            
            updateAllMarkers();

        }, 0L, 100L); // Обновляем маркеры 

    }
}
