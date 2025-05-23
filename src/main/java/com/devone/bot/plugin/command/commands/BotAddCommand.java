package com.devone.bot.plugin.command.commands;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.BotManager;

import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class BotAddCommand implements CommandExecutor {

    private final BotManager botManager;
    private final Random random = new Random();

    // ✅ Список стандартных скинов Minecraft
    private static final List<String> DEFAULT_SKINS = List.of(
        "Steve", "Alex", "Ari", "Kai", "Noor", "Sunny", "Zuri", "Efe", "Makena"
    );

    public BotAddCommand(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Location location = player.getLocation();
        
        // ✅ Выбираем случайный скин и создаём имя
        String skin = getRandomSkin();
        String botName = "AI_" + skin + "_" + random.nextInt(10);

        // ✅ Создаём и спавним NPC
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, botName);
        
        npc.spawn(location);


        // ✅ Устанавливаем скин
        applySkin(npc, skin);

        // ✅ Создаём объект бота и добавляем его в `BotManager`
        botManager.addBot(botName, new Bot(botName, npc, botManager));

        player.sendMessage("§aBot " + botName + " Has been spawned!");

        return true;
    }

    private String getRandomSkin() {
        return DEFAULT_SKINS.get(random.nextInt(DEFAULT_SKINS.size()));
    }

    private void applySkin(NPC bot, String skinName) {
        bot.getOrAddTrait(SkinTrait.class).setSkinName(skinName);
    }
}
