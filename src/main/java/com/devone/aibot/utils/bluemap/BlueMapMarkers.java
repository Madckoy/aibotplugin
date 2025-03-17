package com.devone.aibot.utils.bluemap;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.utils.*;

import com.devone.aibot.web.BotWebService;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;

import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.*;

public class BlueMapMarkers {
    private final BotManager botManager;
    private static final String MARKERS_SET_ID = "blue-map-bot-markers";
    private MarkerSet mSet;
    private final Map<String, Location> lastKnownLocations = new HashMap<>();

    public BlueMapMarkers(BotManager botManager) {
        this.botManager = botManager;

        // BLUE MAP INTEGRATION IS HERE
        BlueMapAPI.onEnable(api -> {

            mSet = setupMarkerSet(api);

            //scheduleMarkerUpdate();

            BotLogger.info("💡 BlueMapAPI detected! Initializing marker system...");

        });
    }

    public MarkerSet getMarkerSet() {
        return mSet;
    }

    public void updateAllMarkers() {

        boolean hasChanges = false;

        List<Bot> bots = List.copyOf(botManager.getAllBots());

        for (Bot bot : bots) {

            Location loc = bot.getNPCCurrentLocation();

            if (loc != null) {
                String botId = bot.getId();
                Location lastLocation = lastKnownLocations.get(botId);

                if (lastLocation != null) {
                    BotLogger.info("💡 " + bot.getId() + " 📍Last known location on map: " + BotStringUtils.formatLocation(lastLocation));

                    // Если позиция не изменилась – пропускаем обновление
                    if (lastLocation.equals(loc)) {
                        BotLogger.info("💡 " + bot.getId() + " 📍 Locations are the same! ");
                        continue;
                    }
                }

                lastKnownLocations.put(botId, loc.clone()); // Обновляем кешированное значение

                hasChanges = true;

                BotMovementLogger.logBotMovement(bot);

                BotLogger.info("📍 " +bot.getId() + "Обновлён маркер бота : " + BotStringUtils.formatLocation(loc));

            } else {
                BotLogger.info("📍 " + bot.getId() + "All Locations are unknown. Skip update.");
            }

        }

        if (hasChanges) {
             updateBlueMapMarkers(bots, lastKnownLocations);

                // 🔥 Форсируем обновление карты ОДИН раз, после завершения цикла
                // Bukkit.getScheduler().runTaskLater(botManager.getPlugin(), () -> {
                //    BotLogger.info("🔄 Форсируем обновление карты!");

                //    updateBlueMapMarkers(bots, lastKnownLocations);
                //
                // }, 100L); // 1-секундная задержка, чтобы не грузить сервер

        }
    }

    public void scheduleMarkerUpdate() {

        Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
            BotLogger.info("✅ Обновление маркеров запущено.");

            updateAllMarkers();

        }, 0L, 100L); // Обновляем маркеры

    }

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

            BotLogger.info("📚 BlueMap marker set initialized.");

            return markerSet;
        } else {
            BotLogger.debug("❌ No valid map found!");
            return null;
        }
    }

    public void updateBlueMapMarkers(List<Bot> bots,  Map<String, Location> lastKnownLocations) {

        if (mSet == null) {
            BotLogger.info("❌ MarkerSet set is not initialized yet!");
            return;
        }

        if (bots.isEmpty()) {
            BotLogger.info("❌ No bots on the Map, skipping update.");
            return;
        }

        //boolean updateTriggered = false;

        for (Bot bot : bots) {

            String botId = bot.getId();
            Location loc = bot.getNPCCurrentLocation();
            UUID botUUID = bot.getUuid();

            // -----------------------------------------------------------------------------------
            // using BlueMapAPI here

            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            // ✅ Get or download the bot’s skin icon
            String skinFilePath = BotUtils.getSkinFile(botUUID);

            POIMarker marker = new POIMarker(botId,  new Vector3d(x, y, z));

            marker.setLabel(botId);

            // ✅ Генерируем абсолютный URL к скину, который хостит AIBotPlugin
            String iconPath = "http://" + BotWebService.SERVER_HOST + ":3000/skins/" + botUUID + ".png";


            marker.setIcon(iconPath, 0,0);
            marker.setLabel(bot.getId());

            mSet.put(botId, marker);

            BotLogger.info("🔄 Updating BlueMap Markers for bot: " + botId + " at " + BotStringUtils.formatLocation(loc));

        }
    }
}
