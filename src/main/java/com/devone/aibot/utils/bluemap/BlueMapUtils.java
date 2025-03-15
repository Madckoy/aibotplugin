
package com.devone.aibot.utils.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;

import de.bluecolored.bluemap.api.markers.POIMarker;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.web.BotWebService;
import com.flowpowered.math.vector.Vector3d;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BlueMapUtils {

    private static final String MARKERS_SET_ID = "bot-markers";


    public static MarkerSet setupMarkerSet(BlueMapAPI api) {

        String worldName = Bukkit.getWorlds().isEmpty() ? "world" : Bukkit.getWorlds().get(0).getName();
        Optional<BlueMapMap> mapOptional = api.getMap(worldName);
    
        if (mapOptional.isPresent()) {
            BlueMapMap map = mapOptional.get();
    
            // Check if the marker set already exists, otherwise create and add a new one
            MarkerSet markerSet = map.getMarkerSets().get(MARKERS_SET_ID);
            if (markerSet == null) {
                markerSet = new MarkerSet(MARKERS_SET_ID); // Create with ID
                map.getMarkerSets().put(MARKERS_SET_ID, markerSet);
            }
    
            BotLogger.info("[BlueMapUtils]: 📚 BlueMap marker set initialized.");
            return markerSet;
        } else {
            BotLogger.info("[BlueMapUtils]: ❌ No valid map found!");
            return null;
        }
    }

    public static void updateBlueMapMarkers(MarkerSet mSet, List<Bot> bots,  Map<String, Location> lastKnownLocations) {
        if (bots.isEmpty()) {
            BotLogger.debug("[BlueMapUtils]: ❌ No bots in list, skipping update.");
            return;
        }

        if (mSet == null) {
            BotLogger.info("Marker set is not initialized yet!");
            return;
        }

        boolean updateTriggered = false;

       // for (Map.Entry<String, Location> entry : lastKnownLocations.entrySet()) {

       for (Bot bot : bots) {

            String botId = bot.getId();
            Location loc = bot.getNPCCurrentLocation();
            UUID botUUID = bot.getUuid();

            // -----------------------------------------------------------------------------------
            // using BlueMapAPI here

            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            String markerId = botId;

            // ✅ Get or download the bot’s skin icon
            String skinFilePath = BotUtils.getSkinFile(botUUID);

            POIMarker marker = new POIMarker(markerId,  new Vector3d(x, y, z));

            marker.setLabel(botId);

            // ✅ Генерируем абсолютный URL к скину, который хостит AIBotPlugin
            String iconPath = "http://" + BotWebService.SERVER_HOST + ":3000/skins/" + botUUID + ".png";


            marker.setIcon(iconPath, 0,0); 
            marker.setLabel(bot.getId());
            
            mSet.put(markerId, marker);
            BotLogger.info("Updated bot marker: " + botId + " at X:" + x + " Y:" + y + " Z:" + z);                
            //--------------------------------------------------------------------------------------

            updateTriggered = true;

            BotLogger.debug("[BlueMapUtils] 🔄 Updating BlueMap Marjers for bot: " + botId +
                    " at X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());

            BotLogger.info("[BlueMapUtils] ✅ Map updated for bots.");        
        }
    }
}
