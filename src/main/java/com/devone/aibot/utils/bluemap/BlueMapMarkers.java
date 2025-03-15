package com.devone.aibot.utils.bluemap;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotMovementLogger;
import com.devone.aibot.utils.Constants;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.MarkerSet;
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
        this.markersFile = new File(Constants.PLUGIN_PATH+"/markers.yml");

        if (!markersFile.exists()) {
            try {
                markersFile.createNewFile();
                BotLogger.debug("[BlueMapMarkers] markers.yml не найден, создан новый файл.");
            } catch (IOException e) {
                BotLogger.error("[BlueMapMarkers] Ошибка создания markers.yml: " + e.getMessage());
            }
        }

        // BLUE MAP INTEGRATION IS HERE
        BlueMapAPI.onEnable(api -> {

            MarkerSet mSet = BlueMapUtils.setupMarkerSet(api);

            scheduleMarkerUpdate(mSet);
            
            BotLogger.info("BlueMapAPI detected! Initializing marker system...");

        });
    }

    private void updateAllMarkers(MarkerSet mSet) {
        if (!markersFile.exists()) {
            BotLogger.error("[BlueMapMarkers] ❌ markers.yml отсутствует! Перезапустите сервер или создайте файл вручную.");
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
            BotLogger.debug("[BlueMapMarkers] ⚠ Нет ботов, markers.yml не обновляется.");
            return;
        }
    
        for (Bot bot : bots) {

            Location loc = bot.getNPCCurrentLocation(); 

            if (loc != null) {
                String botId = bot.getId();
                Location lastLocation = lastKnownLocations.get(botId);

                if( lastLocation!=null ) {
                    BotLogger.debug("[BlueMapMarkers]" + bot.getId() +" 📍Last known location on map: " +
                            " X:" + lastLocation.getBlockX() + " Y:" + lastLocation.getBlockY() + " Z:" + lastLocation.getBlockZ());
                    

                    // Если позиция не изменилась – пропускаем обновление
                    if (lastLocation.equals(loc)) {
                        BotLogger.debug("[BlueMapMarkers]" + bot.getId() +" 📍 Locations are the same! ");
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
    
                BotLogger.debug("[BlueMapMarkers]" + bot.getId() +" 📍 Обновлён маркер бота : " +
                        " X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());

            } else {
                BotLogger.debug("[BlueMapMarkers]" + bot.getId() + " 📍 ALL Bot Locations are unknown. Skip update.");
            }
           
        }
    
        if (hasChanges) { 
            try {
                config.save(markersFile);
                BotLogger.debug("[BlueMapMarkers] ✅ markers.yml обновлён!");


                // 🔥 Форсируем обновление карты ОДИН раз, после завершения цикла
                Bukkit.getScheduler().runTaskLater(botManager.getPlugin(), () -> {
                    BotLogger.info("[BlueMapMarkers] 🔄 Форсируем обновление карты!");

                    BlueMapUtils.updateBlueMapMarkers(mSet, bots, lastKnownLocations);
   
   
            }, 20L); // 1-секундная задержка, чтобы не грузить сервер


            } catch (IOException e) {
                BotLogger.error("[BlueMapMarkers] ❌ Ошибка при сохранении marker: " + e.getMessage());
            }
        }
    }

    public void scheduleMarkerUpdate( MarkerSet mSet ) {

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            BotLogger.debug("[BlueMapMarkers] ✅ Обновление маркеров запущено.");
            
            updateAllMarkers( mSet );

        }, 0L, 100L); // Обновляем маркеры 

    }
}
