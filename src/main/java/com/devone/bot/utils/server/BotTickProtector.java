package com.devone.bot.utils.server;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class BotTickProtector {

    private static final String NPC_NAME = "TickKeeper";
    private static final Location DEFAULT_LOCATION = new Location(Bukkit.getWorlds().get(0), 0, 200, 0);

    public static void ensureTickAlive(JavaPlugin plugin) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            int id = getExistingTickKeeperId();

            if (id != -1) {
                NPC keeper = CitizensAPI.getNPCRegistry().getById(id);
                if (!keeper.isSpawned()) {
                    keeper.spawn(DEFAULT_LOCATION);
                    log("✅ TickKeeper найден и заспавнен.");
                }
                return;
            }

            // Создание нового NPC
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, NPC_NAME);
            npc.spawn(DEFAULT_LOCATION);

            npc.data().set("persistent", true);
            npc.data().set("disable-skins", true);
            npc.data().set("cached-skin-uuid-name", "Steve"); // <-- ключевая строка
            npc.data().set("playerlist", true);
            npc.data().set("nameplate-visible", false);
            npc.data().set("invisible", true);

            // ✅ Задать скин через SkinTrait
            SkinTrait skin = npc.getOrAddTrait(SkinTrait.class);
            skin.setSkinName("Notch ");

            log("✨ TickKeeper создан и заспавнен.");
        });
    }

    private static int getExistingTickKeeperId() {
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (NPC_NAME.equalsIgnoreCase(npc.getName())) {
                return npc.getId();
            }
        }
        return -1;
    }

    private static void log(String msg) {
        Bukkit.getLogger().info("[TickKeeper] " + msg);
    }
}
