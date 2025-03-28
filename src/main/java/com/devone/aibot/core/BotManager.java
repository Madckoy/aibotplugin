package com.devone.aibot.core;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.bluemap.BlueMapMarkers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BotManager {

    private final AIBotPlugin plugin;
    private final Map<String, Bot> botsMap = new HashMap<>();
    private final Map<UUID, Bot> selectedBots = new HashMap<>();

    private BlueMapMarkers bm_markers;
    
    public BotManager(AIBotPlugin plugin) {
        this.plugin = plugin;

        // ✅ Теперь вызываем `loadExistingBots()` напрямую в единственном `runTaskLater`
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BotLogger.info(true, "💡 Загружаем ботов...");

            loadExistingBots(); // ✅ Загружаем ботов из CitizensAPI и `bots.yml`

            BotLogger.info(true, "✅ Все боты загружены.");
            
        }, 600L);
    }

    public Bot getBot(String name) {
        return botsMap.get(name);
    }

    public void addBot(String name, Bot bot) {
        botsMap.put(name, bot);
        saveBots(); // ✅ Сохраняем при добавлении
    }

    public void removeBot(String name) {
        Bot bot = getBot(name);
        if (bot != null) {

            bot.despawnNPC();  // Деспавн и очистка стека задач вызывается из самого бота

            BotLogger.debug(true, "➖" + name + " был удалён.");

            saveBots(); // Сохраняем список после удаления одного бота

            // update Map Markers
            botsMap.remove(name); // Удаляем из списка маркероа
            // refresh map markers
            bm_markers.removeMarker(name);
        }
    }

    public boolean botExists(String name) {
        return botsMap.containsKey(name);
    }

    public Collection<Bot> getAllBots() {
        return botsMap.values();
    }

    private void loadExistingBots() {
        BotLogger.info(true, "🔄 Загружаем существующих NPC ботов...");

        File file = new File(BotConstants.PLUGIN_PATH_CONFIGS, "BotManager.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc == null || !npc.getName().startsWith("AI_"))
                continue;

            String botName = npc.getName();
            // UUID npcUUID = npc.getUniqueId();

            if (botExists(botName))
                continue;

            // Set defaults
            Location storedLocation = BotUtils.getFallbackLocation();

            // Загружаем координаты, если они сохранены
            if (config.contains("bots." + botName)) {
                String path = "bots." + botName;

                int x = config.getInt(path + ".x", storedLocation.getBlockX());
                int y = config.getInt(path + ".y", storedLocation.getBlockY());
                int z = config.getInt(path + ".z", storedLocation.getBlockZ());

                storedLocation = new Location(Bukkit.getWorlds().get(0), x, y, z);
            }

            BotLogger.info(true, "♻️ " + botName + " Has been loaded.");

            if (!npc.isSpawned()) {

                npc.spawn(storedLocation);

                BotLogger.info(true, "✅ " + botName + " a new NPC has been spawned.");
            }    
            
            Bot bot = new Bot(botName, npc, this);
            botsMap.put(botName, bot);
            BotLogger.info(true, "✅" + bot.getId() + " Added to the Map!");
            
        }

        BotLogger.info(true, "✅ Загружено NPC ботов: " + botsMap.size());

        // ✅ После загрузки сразу обновляем маркеры
        BotLogger.info(true, "✅ Обновляем карту: " );

         bm_markers= new BlueMapMarkers(this);
         bm_markers.scheduleMarkerUpdate();

    }

    public void saveBots() {

        File file = new File(BotConstants.PLUGIN_PATH_CONFIGS, "BotManager.yml");
        FileConfiguration config = new YamlConfiguration();

        for (Bot bot : botsMap.values()) {
            String path = "bots." + bot.getId();
            config.set(path + ".uuid", bot.getUuid().toString());

            Location loc = bot.getRuntimeStatus().getCurrentLocation();

            // Сохраняем координаты в BotManager.yml
            config.set("bots." + bot.getId() + ".x", loc.getBlockX());
            config.set("bots." + bot.getId() + ".y", loc.getBlockY());
            config.set("bots." + bot.getId() + ".z", loc.getBlockZ());

        }

        try {
            config.save(file);

            BotLogger.info(true, "✅ Боты сохранены в BotManager.yml.");

        } catch (IOException e) {
            BotLogger.error(true, "⚠️ Ошибка сохранения BotManager.yml: " + e.getMessage());
        }
    }

    public void removeAllBots() {
        for (String botId : new ArrayList<>(botsMap.keySet())) { // Используем keySet(), чтобы передавать только идентификаторы

            removeBot(botId); // Вызываем стандартный метод удаления
        }
        BotLogger.info(true, "✅ Все боты удалены.");
    }

    public void selectBot(UUID playerUUID, Bot bot) {
        selectedBots.put(playerUUID, bot);
    }

    public boolean unselectBot(UUID playerUUID) {
        if (!selectedBots.containsKey(playerUUID)) {
            return false; // Бот не был выбран
        }
    
        selectedBots.remove(playerUUID);
        return true; // Бот успешно сброшен
    }

    public Bot getOrSelectBot(UUID playerId) {
        Bot bot = getSelectedBot(playerId);

        // ✅ Если бот не выбран, но есть только один, выбираем его
        if (bot == null && botsMap.size() == 1) {
            bot = botsMap.values().iterator().next();
            selectBot(playerId, bot);
        }

        return bot;
    }

    public Bot getSelectedBot(UUID playerUUID) {
        return selectedBots.get(playerUUID);
    }

    public AIBotPlugin getPlugin(){
        return plugin;
    }

    /**
     * Проверка, является ли Entity ботом (перебором ботов)
     */
    public boolean isBot(Entity entity) {
        return botsMap.values().stream()
            .filter(bot -> bot.getNPCEntity() != null) // ✅ Фильтруем null-значения
            .anyMatch(bot -> bot.getNPCEntity().getUniqueId().equals(entity.getUniqueId()));
    }
}
