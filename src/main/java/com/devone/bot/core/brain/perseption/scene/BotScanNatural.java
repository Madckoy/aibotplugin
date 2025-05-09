package com.devone.bot.core.brain.perseption.scene;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.devone.bot.core.Bot;
import com.devone.bot.core.utils.logger.BotLogger;

public class BotScanNatural {

public static List<Entity> scan(Bot bot, double radius) {
        return bot.getNPCEntity().getNearbyEntities(radius, radius, radius);
    }

    public static void logScanNatural(Bot bot, double radius) {
        List<Entity> nearbyEntities = BotScanNatural.scan(bot, radius);
    
        if (nearbyEntities.isEmpty()) {
            BotLogger.debug("🔍", true, bot.getId()+" В радиусе " + radius + " блоков нет НИЧЕГО.");
            return;
        }
    
        BotLogger.debug("🔍", true, bot.getId()+" В радиусе " + radius + " блоков есть:");

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                ItemStack item = ((Item) entity).getItemStack();
                BotLogger.debug("🔍" , true, bot.getId() + " 🎁 " + item.getAmount() + "x " + item.getType());
            } else {
                BotLogger.debug("🔍", true,  bot.getId() + " 😈 " + entity.getType() + " (" + entity.getName() + ")");
            }
        }
    }


}
