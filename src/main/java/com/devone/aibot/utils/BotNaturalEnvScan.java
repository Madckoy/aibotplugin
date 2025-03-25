package com.devone.aibot.utils;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.devone.aibot.core.Bot;

public class BotNaturalEnvScan {

public static List<Entity> scanNearbyNatural(Bot bot, double radius) {
        return bot.getNPCEntity().getNearbyEntities(radius, radius, radius);
    }

    public static void logScanNatural(Bot bot, double radius) {
        List<Entity> nearbyEntities = BotNaturalEnvScan.scanNearbyNatural(bot, radius);
    
        if (nearbyEntities.isEmpty()) {
            BotLogger.trace(true, "💡 " + bot.getId()+" В радиусе " + radius + " блоков нет НИЧЕГО.");
            return;
        }
    
        BotLogger.trace(true, "💡 "+ bot.getId()+" В радиусе " + radius + " блоков есть:");

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                ItemStack item = ((Item) entity).getItemStack();
                BotLogger.trace(true, "🎁 " + bot.getId() +" " + item.getAmount() + "x " + item.getType());
            } else {
                BotLogger.trace(true, "🔹 " + bot.getId() + " " + entity.getType() + " (" + entity.getName() + ")");
            }
        }
    }


}
