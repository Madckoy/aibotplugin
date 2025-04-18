package com.devone.bot.utils.bluemap;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.BotManager;
import com.devone.bot.utils.blocks.BotLocation;
import com.devone.bot.utils.image.BotImageUtils;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.web.BotWebService;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;

import de.bluecolored.bluemap.api.BlueMapMap;

import de.bluecolored.bluemap.api.markers.MarkerSet;

import de.bluecolored.bluemap.api.markers.POIMarker;

import java.util.*;

import org.bukkit.Bukkit;

public class BlueMapMarkers {
    private final BotManager botManager;
    private static final String MARKERS_SET_ID = "blue-map-bot-markers";
    private MarkerSet mSet;
    private final Map<String, BotLocation> lastKnownLocations = new HashMap<>();

    public BlueMapMarkers(BotManager botManager) {
        this.botManager = botManager;

        // BLUE MAP INTEGRATION IS HERE
        BlueMapAPI.onEnable(api -> {

            mSet = setupMarkerSet(api);

            //scheduleMarkerUpdate();

            BotLogger.info("💡", true, "BlueMapAPI detected! Initializing marker system...");

        });
    }

    public MarkerSet getMarkerSet() {
        return mSet;
    }

    public void updateAllMarkers() {

        if (mSet == null) {
            BotLogger.warn("⚠️", true, "Маркер-сет ещё не инициализирован — пропускаем обновление.");
            return;
        }

        mSet.remove(MARKERS_SET_ID);

        boolean hasChanges = false;

        List<Bot> bots = List.copyOf(botManager.getAllBots());

        for (Bot bot : bots) {

            BotLocation loc = bot.getNavigation().getLocation();

            if (loc != null) {
                String botId = bot.getId();
                BotLocation lastLocation = lastKnownLocations.get(botId);

                if (lastLocation != null) {
                    BotLogger.info("💡", true,  bot.getId() + "Last known location on map: " + lastLocation);

                    // Если позиция не изменилась – пропускаем обновление
                    if (lastLocation.equals(loc)) {
                        BotLogger.info("💡", true, bot.getId() + "Locations are the same! ");
                        continue;
                    }
                }

                lastKnownLocations.put(botId, new BotLocation(loc)); // Обновляем кешированное значение

                hasChanges = true;

                BotLogger.info("📍", true, bot.getId() + "Обновлён маркер бота : " + loc);

            } else {
                BotLogger.info("📍", true, bot.getId() + "All Locations are unknown. Skip update.");
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
            BotLogger.info("✅ ", true, "Обновление маркеров запущено.");

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

            BotLogger.info("📚 ",true, "BlueMap marker set initialized.");

            return markerSet;
        } else {
            BotLogger.info("❌", true, "No valid map found!");
            return null;
        }
    }

    public void updateBlueMapMarkers(List<Bot> bots,  Map<String, BotLocation> lastKnownLocations) {

        if (mSet == null) {
            BotLogger.info("❌", true, "MarkerSet set is not initialized yet!");
            return;
        }

        if (bots.isEmpty()) {
            BotLogger.info("❌", true, "No bots on the Map, skipping update.");
            return;
        }

        //boolean updateTriggered = false;

        for (Bot bot : bots) {

            String botId = bot.getId();
            BotLocation loc = bot.getNavigation().getLocation();
            UUID botUUID = bot.getUuid();

            // -----------------------------------------------------------------------------------
            // using BlueMapAPI here

            int x = loc.getX();
            int y = loc.getY();
            int z = loc.getZ();

            // ✅ Get or download the bot’s skin icon
            @SuppressWarnings("unused")
            String skinFilePath = BotImageUtils.getSkinFile(botUUID);

            POIMarker marker = new POIMarker(botId,  new Vector3d(x, y, z));

            marker.setLabel(botId);

            // ✅ Генерируем абсолютный URL к скину, который хостит AIBotPlugin
            String iconPath = "http://" + BotWebService.getInstance().SERVER_HOST + ":3000/skins/" + botUUID + ".png";


            marker.setIcon(iconPath, 0,0);
            marker.setLabel(bot.getId());

            mSet.put(botId, marker);

            BotLogger.info("🔄", true, "Updating BlueMap Markers for bot: " + botId + " at " + loc);

        }
    }

    public void removeMarker(String bId) {
        if (mSet!=null) {
            mSet.remove(bId);
        }
    }
}
