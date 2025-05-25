package com.devone.bot.core.web.bluemap;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.web.BotWebService;
import com.devone.bot.core.web.image.BotImageUtils;
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
    private final Map<String, BotPosition> lastKnownLocations = new HashMap<>();

    public BlueMapMarkers(BotManager botManager) {
        this.botManager = botManager;

        // BLUE MAP INTEGRATION IS HERE
        BlueMapAPI.onEnable(api -> {

            mSet = setupMarkerSet(api);

            BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), "💡 BlueMapAPI detected! Initializing marker system...");

        });
    }

    public MarkerSet getMarkerSet() {
        return mSet;
    }

    public void updateAllMarkers() {

        if (mSet == null) {
            BotLogger.warn("🗺️", AIBotPlugin.getInstance().isLogged(), "⚠️ Маркер-сет ещё не инициализирован — пропускаем обновление.");
            return;
        }

        mSet.remove(MARKERS_SET_ID);

        boolean hasChanges = false;

        List<Bot> bots = List.copyOf(botManager.getAllBots());

        for (Bot bot : bots) {

            BotPosition loc = bot.getNavigator().getPosition();

            if (loc != null) {
                String botId = bot.getId();
                BotPosition lastLocation = lastKnownLocations.get(botId);

                if (lastLocation != null) {
                    BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(),  bot.getId() + " 📌 Last known location on map: " + lastLocation);

                    // Если позиция не изменилась – пропускаем обновление
                    if (lastLocation.equals(loc)) {
                        BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), bot.getId() + " ❓ Locations are the same! ");
                        continue;
                    }
                }

                lastKnownLocations.put(botId, new BotPosition(loc)); // Обновляем кешированное значение

                hasChanges = true;

                BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), bot.getId() + " 📍 Обновлён маркер бота : " + loc);

            } else {
                BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), bot.getId() + " 📍 All Locations are unknown. Skip update.");
            }

        }

        if (hasChanges) {
             updateBlueMapMarkers(bots, lastKnownLocations);

                // 🔥 Форсируем обновление карты ОДИН раз, после завершения цикла
                // Bukkit.getScheduler().runTaskLater(botManager.getPlugin(), () -> {
                //    BotLogger.debug("🔄 Форсируем обновление карты!");

                //    updateBlueMapMarkers(bots, lastKnownLocations);
                //
                // }, 100L); // 1-секундная задержка, чтобы не грузить сервер

        }
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

            BotLogger.debug("🗺️",AIBotPlugin.getInstance().isLogged(), "📚 BlueMap marker set initialized.");

            return markerSet;
        } else {
            BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), "❌ No valid map found!");
            return null;
        }
    }

    public void updateBlueMapMarkers(List<Bot> bots,  Map<String, BotPosition> lastKnownLocations) {

        if (mSet == null) {
            BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), "❌ MarkerSet set is not initialized yet!");
            return;
        }

        if (bots.isEmpty()) {
            BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), "❌ No bots on the Map, skipping update.");
            return;
        }

        //boolean updateTriggered = false;

        for (Bot bot : bots) {

            String botId = bot.getId();
            BotPosition loc = bot.getNavigator().getPosition();
            UUID botUUID = bot.getUuid();

            // -----------------------------------------------------------------------------------
            // using BlueMapAPI here

            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();

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

            BotLogger.debug("🗺️", AIBotPlugin.getInstance().isLogged(), botId +" ♻️ Updating BlueMap Markers for bot: " +  " at " + loc);

        }
    }

    public void removeMarker(String bId) {
        if (mSet!=null) {
            mSet.remove(bId);
        }
    }
}
