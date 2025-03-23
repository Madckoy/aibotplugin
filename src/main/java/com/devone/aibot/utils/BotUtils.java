package com.devone.aibot.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class BotUtils {

    public static String getBlockName(Block bl) {

        String text = bl.toString();

        String result = "";
        // Regular expression to capture the value of type
        String pattern = "type=([A-Za-z0-9_]+)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Create a matcher object
        Matcher m = r.matcher(text);

        // Check if the pattern is found
        if (m.find()) {
            result = m.group(1);
        }

        return result; 
    }

    public static void playBlockBreakEffect(Location location) {
        if (location == null || location.getWorld() == null) return;
    
        Material blockType = location.getBlock().getType();
    
        // ✅ Проверяем, что блок не AIR (иначе эффект не сработает)
        if (blockType == Material.AIR) {
            BotLogger.trace("⚠️ Эффект разрушения отменён: блок уже AIR " + BotStringUtils.formatLocation(location));
            return;
        }
    
        location.getWorld().spawnParticle(
            org.bukkit.Particle.BLOCK_CRACK,
            location.clone().add(0.5, 0.5, 0.5), // Центр блока
            20, // Кол-во частиц
            0.25, 0.25, 0.25, // Разброс
            location.getBlock().getBlockData() // Тип блока для эффекта
        );
    
        BotLogger.trace("🎇 Эффект разрушения воспроизведён на " + BotStringUtils.formatLocation(location));
    }

    public static boolean requiresTool(Material blockType) {
        return switch (blockType) {
            case IRON_ORE, GOLD_ORE, DIAMOND_ORE, DEEPSLATE, OBSIDIAN -> true; // ❗ Только эти блоки требуют инструмент
            default -> false; // Всё остальное можно ломать руками
        };
    }
    
    public static Location getFallbackLocation() {
        World world = Bukkit.getWorlds().get(0);
        return world.getSpawnLocation();
    }
    public static boolean isBreakableBlock(Location location) {
        if (location == null || location.getWorld() == null) return false;
    
        Material type = location.getBlock().getType();
    
        return switch (type) {
            case AIR, CAVE_AIR, VOID_AIR,
                 BEDROCK, BARRIER,
                 END_PORTAL, END_PORTAL_FRAME,
                 STRUCTURE_BLOCK, STRUCTURE_VOID,
                 COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK -> false;
            default -> true;
        };
    }
    

}