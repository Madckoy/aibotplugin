
package com.devone.aibot.utils.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.devone.aibot.utils.BotLogger;

import de.bluecolored.bluemap.api.BlueMapAPI;

import java.util.Map;
import java.util.Optional;

public class BlueMapUtils {

    public static void updateBlueMapMarkers(Map<String, Location> lastKnownLocations) {
        if (lastKnownLocations.isEmpty()) {
            BotLogger.debug("[BlueMapUtils] ❌ No known bot locations, skipping update.");
            return;
        }

        boolean updateTriggered = false;

        for (Map.Entry<String, Location> entry : lastKnownLocations.entrySet()) {
            String botId = entry.getKey();
            Location loc = entry.getValue();

            if (loc == null) {
                BotLogger.debug("[BlueMapUtils] ⚠ " + botId + " has no known location, skipping.");
                continue;
            }

            //
            // use BlueMapAPI here
            //
        
            Optional<BlueMapAPI> optionalApi = BlueMapAPI.getInstance();


            BlueMapAPI.getInstance().ifPresent(api -> {
                //code executed when the api is enabled (skipped if the api is not enabled)
                Optional<BlueMapMap> mapOptional = api.getMap("world");



             });
            
            //

            updateTriggered = true;

            BotLogger.debug("[BlueMapUtils] 🔄 Updating BlueMap Marjers for bot: " + botId +
                    " at X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
        }

        if (updateTriggered) {
            BotLogger.info("[BlueMapUtilss] ✅ Map updated for bots.");
        }
    }
}
